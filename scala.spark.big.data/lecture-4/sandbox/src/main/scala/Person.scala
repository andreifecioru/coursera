case class Person(
  id: Long,
  firstName: String,
  lastName: String,
  age: Int,
  address: Option[String]
)

case class Purchase(
  personId: Long,
  amount: Long
)

object Data {
  val persons = Seq(
    Person(1L, "Andrei", "Popescu", 30, Some("Bucharest")),
    Person(2L, "Bogdan", "Cucu", 20, Some("Bucharest")),
    Person(3L, "Ion", "Georgescu", 28, Some("Ploiesti")),
    Person(4L, "Vali", "Tudose", 18, Some("Ploiesti")),
    Person(5L, "Adi", "Dorian", 8, Some("Ploiesti")),
    Person(6L, "Radu", "Ionescu", 55, None),
    Person(7L, "Gigi", "Dudu", 10, Some("Constanta"))
  )

  val purchases = Seq(
    Purchase(1L, 100L),
    Purchase(1L, 300L),
    Purchase(2L, 400L),
    Purchase(2L, 40L),
    Purchase(2L, 50L),
    Purchase(5L, 500L),
    Purchase(5L, 600L),
    Purchase(6L, 6000L),
    Purchase(7L, 60L),
    Purchase(7L, 10L)
  )

}
