package vinted

import vinted.Constants.NB_ITEMS_BY_MONTH
import vinted.SwitchType.*
import better.files.File

object SwitchExplorer {

  def explore(outputPath: String, simulation: Simulation) =

    val switchers =
      Seq(
        Seq(),
        Seq(SwitchType.AverageDistance(75)),
        Seq(SwitchType.TransportCO2Intensity(strategy.TransportCO2Intensity.Constant(0.005))),
        Seq(SwitchType.MaximumItemsBySeller(1)),
        Seq(SwitchType.MaximumItemsByBuyer(0.5)),
        Seq(SwitchType.ReplacementRatio(0.75)),
        Seq(SwitchType.ImpulsiveRatio(0.15)),
        Seq(SwitchType.SecondHandItemsRatio(0.9)),
        Seq(SwitchType.ReinvestementInNewRatio(0.05)),
        Seq(SwitchType.ReinvestementInPlatformRatio(0.65)),
        Seq(SwitchType.AttractivenessForSellers(0.005)),
        Seq(SwitchType.AttractivenessForBuyers(0.005)),
        Seq(SwitchType.ReinvestmentDelay(3)),
        Seq(SwitchType.MaximumItemsBySeller(1), SwitchType.AttractivenessForSellers(-0.005)), // Seller quota
        Seq(SwitchType.MaximumItemsByBuyer(0.5), SwitchType.AttractivenessForBuyers(-0.005)), // Buyer quota
        Seq(
          SwitchType.ReinvestementInNewRatio(0.0),
          SwitchType.ReinvestementInPlatformRatio(0.68),
          SwitchType.AttractivenessForSellers(-0.005),
          SwitchType.ImpulsiveRatio(0.35)
        ) // Virtual currency
      )

    val switches = switchers.map(s=> Some(Switch(s, 20)))

    val dynamics = switches map : s =>
      println("Run " + s.toString)
      val states = Simulation.run(simulation, s)

      Seq(
        states.map(_.sales),
        states.map(_.co2Emission)
      )

    val transposed = dynamics.transpose

    val dir = File(outputPath)

    transposed.zip(Seq("Sales", "CO2")).foreach: (metrics, label) =>
      val file = File(s"${outputPath}/${label}.csv")
      val content =
        val headers =
          Seq(
            "Base",
            "Average distance",
            "Transport CO2 intensity",
            "Max items by salers",
            "Max items by buyer",
            "Replacement ratio",
            "Impulsive ratio",
            "Second hand items ratio",
            "Reinvestment in new ratio",
            "Reinvestement in platform natio",
            "Attractiveness for sellers",
            "Attractiveness for buyers",
            "Reinvestment delay",
            "Seller quota",
            "Buyer quota",
            "Virtual currency"
          )
        (headers zip metrics.map(_.map(_.toString))).map((h, t) => h +: t).transpose.map(_.mkString(",")).mkString("\n")

      file.overwrite(content)
}
