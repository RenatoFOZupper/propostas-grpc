package br.com.zup.edu.shared.exceptions

import br.com.zup.edu.CreateProposalEndpoint
import br.com.zup.edu.shared.ErrorHandler
import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor : MethodInterceptor<CreateProposalEndpoint, Any?> {

    val LOGGER = LoggerFactory.getLogger(this.javaClass)

    override fun intercept(context: MethodInvocationContext<CreateProposalEndpoint, Any?>): Any? {
        //antes
        LOGGER.info("Intercepting method: ${context.targetMethod}")

        try {
            return context.proceed() // processa o metodo interceptado
        } catch (e: Exception) {

            val error = when(e) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message).asRuntimeException()
                is ProposalAlreadyExistsException -> Status.ALREADY_EXISTS.withDescription(e.message).asRuntimeException()
                is ConstraintViolationException -> HandleConstraintViolationException(e)
                else -> Status.UNKNOWN.withDescription("unexpected error happened").asRuntimeException()
            }

            val responseObserver = context.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(error)

            return null
        }

        //depois
    }

    private fun HandleConstraintViolationException(e: ConstraintViolationException): StatusRuntimeException {
        val details = BadRequest.newBuilder()
            .addAllFieldViolations(e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                    .setField(it.propertyPath.last().name) //save.entity.document
                    .setDescription(it.message) //must be blank
                    .build()
            })
            .build() // cria lista de violations

        val statusProto = com.google.rpc.Status.newBuilder()
            .setCode(Code.INVALID_ARGUMENT_VALUE)
            .setMessage("invalid parameters")
            .addDetails(com.google.protobuf.Any.pack(details)) // empacota Message do protobuf
            .build()

        LOGGER.info("$statusProto") //Na atual versão do BloomRPC, não é possivel visualizar os metadados de erro da resposta
        val error = StatusProto.toStatusRuntimeException(statusProto)
        return error
    }
}