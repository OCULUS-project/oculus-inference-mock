package pl.poznan.put.oculus.inferencemock

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import pl.poznan.put.oculus.inferencemock.model.GrfIrf
import pl.poznan.put.oculus.inferencemock.model.JobEvent
import pl.poznan.put.oculus.inferencemock.model.ResultFact
import pl.poznan.put.oculus.inferencemock.model.ResultFactEvent

@SpringBootApplication
class OculusInferenceMockApplication

fun main(args: Array<String>) {
    runApplication<OculusInferenceMockApplication>(*args)
}

@Component
class Mock (
        private val factsKafkaTemplate: KafkaTemplate<String, ResultFactEvent>
) {
    @KafkaListener(topics = ["jobs"], groupId = "3")
    fun receive(event: JobEvent) {
        if(event.type == "INFERENCE_STARTED") {
            logger.info("received INFERENCE_STARTED job event for job ${event.jobId}")
            generateRandomFacts(event.jobId)
        }
    }

    private fun generateRandomFacts(jobId: String) {
        val n = (20_000..40_000).random()
        val facts = mutableListOf<ResultFact>()
        repeat(n) {

            facts.add(ResultFact(
                    "head_$it",
                    listOf("set_$it"),
                    false,
                    GrfIrf((1..100).random() / 100.0, (1..100).random() / 100.0)
            ))

            if (facts.size >= 100) {
                factsKafkaTemplate.send("resultFacts", ResultFactEvent(facts.toList(), jobId, false))
                facts.clear()
            }
        }
        factsKafkaTemplate.send("resultFacts", ResultFactEvent(facts, jobId, true))
        logger.info("generated $n random facts for job $jobId")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Mock::class.java)
    }
}
