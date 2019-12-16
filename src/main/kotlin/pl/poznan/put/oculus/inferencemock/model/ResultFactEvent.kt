package pl.poznan.put.oculus.inferencemock.model

data class ResultFactEvent (
        val facts: List<ResultFact>,
        val job: String,
        val last: Boolean
)

data class ResultFact (
        val head: String,
        val set: List<String>,
        val conjunction: Boolean,
        val grfIrf: GrfIrf
)

data class GrfIrf(
        val grf: Double,
        val irf: Double
)
