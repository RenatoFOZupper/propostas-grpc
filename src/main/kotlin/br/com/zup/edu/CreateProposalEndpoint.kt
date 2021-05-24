package br.com.zup.edu

import br.com.zup.edu.createproposal.Proposal
import br.com.zup.edu.createproposal.ProposalRepository
import com.google.protobuf.Any
import com.google.protobuf.Timestamp
import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.ZoneId
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException


@Singleton
open class CreateProposalEndpoint(val repository: ProposalRepository) : PropostasGrpcServiceGrpc.PropostasGrpcServiceImplBase(){

    val LOGGER = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    open override fun create( request: CreateProposalRequest, responseObserver: StreamObserver<CreateProposalResponse>) {

        LOGGER.info("new request: $request")

        val proposal = Proposal(name = request.name,
                                document = request.document,
                                email = request.email,
                                address = request.address,
                                salary = BigDecimal(request.salary))

        try {
            repository.save(proposal)
        } catch (e: ConstraintViolationException) {
            LOGGER.error("Erro de validação: ${e.message}")

            val violations = e.constraintViolations.map {
                BadRequest.FieldViolation.newBuilder()
                                        .setField(it.propertyPath.last().name) //save.entity.document
                                        .setDescription(it.message) //must be blank
                                        .build()
            }

            val details = BadRequest.newBuilder().addAllFieldViolations(violations).build() // cria lista de violations

            val statusProto = com.google.rpc.Status.newBuilder()
                                                    .setCode(Code.INVALID_ARGUMENT_VALUE)
                                                    .setMessage("invalid parameters")
                                                    .addDetails(Any.pack(details)) // empacota Message do protobuf
                                                    .build()

            LOGGER.info("$statusProto") // Na atual versão do BloomRPC, não é possivel visualizar os metadados de erro da resposta
            responseObserver.onError(StatusProto.toStatusRuntimeException(statusProto))
            return
        }


        val response = CreateProposalResponse.newBuilder()
            .setId(proposal.id.toString())
            .setCreatedAt(proposal.createdAt.let {
                val instant = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(instant.epochSecond)
                    .setNanos(instant.nano)
                    .build()
            })
            .build()

        /* Primeiro modelo - retorna um id fake e uma data fake
        val response = CreateProposalResponse.newBuilder()
                                                .setId(UUID.randomUUID().toString())
                                                .setCreatedAt(LocalDateTime.now().let {
                                                    val instant = it.atZone(ZoneId.of("UTC")).toInstant()
                                                    Timestamp.newBuilder()
                                                        .setSeconds(instant.epochSecond)
                                                        .setNanos(instant.nano)
                                                        .build()
                                                })
                                                .build()*/

        responseObserver.onNext(response)
        responseObserver.onCompleted()


    }

}