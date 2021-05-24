package br.com.zup.edu

import br.com.zup.edu.createproposal.Proposal
import br.com.zup.edu.createproposal.ProposalRepository
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.ZoneId
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
open class CreateProposalEndpoint(val repository: ProposalRepository) : PropostasGrpcServiceGrpc.PropostasGrpcServiceImplBase(){

    val logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    open override fun create(@Valid request: CreateProposalRequest, responseObserver: StreamObserver<CreateProposalResponse>) {

        logger.info("new request: $request")

        val newProposal = Proposal(name = request.name,
                                document = request.document,
                                email = request.email,
                                address = request.address,
                                salary = BigDecimal(request.salary))

        val proposal = repository.save(newProposal)

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