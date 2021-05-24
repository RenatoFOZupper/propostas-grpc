package br.com.zup.edu

import br.com.zup.edu.createproposal.Proposal
import br.com.zup.edu.createproposal.ProposalRepository
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.ZoneId
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException
import javax.validation.Valid


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
            responseObserver.onError(Status.INVALID_ARGUMENT
                                        .withDescription("invalid parameters")
                                        .asRuntimeException())
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