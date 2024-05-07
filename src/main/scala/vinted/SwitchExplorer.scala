package vinted

import vinted.Constants.NB_ITEMS_BY_MONTH
import vinted.Switcher.*
import better.files.File

object SwitchExplorer {

  def explore(outputPath: String, simulation: Simulation) =

    val switchers =
      Seq(
        None,
        Some(Switcher(SwitchType.AverageDistance(75), 20)),
        Some(Switcher(SwitchType.TransportCO2Intensity(strategy.TransportCO2Intensity.Constant(0.005)), 20)),
        Some(Switcher(SwitchType.MaximumItemsBySeller(1), 20)),
        Some(Switcher(SwitchType.MaximumItemsByBuyer(0.5), 20)),
        Some(Switcher(SwitchType.ReplacementRatio(0.75), 20)),
        Some(Switcher(SwitchType.ImpulsiveRatio(0.15), 20)),
        Some(Switcher(SwitchType.SecondHandItemsRatio(0.9), 20)),
        Some(Switcher(SwitchType.ReinvestementInNewRatio(0.05), 20)),
        Some(Switcher(SwitchType.ReinvestementInPlatformRatio(0.65), 20)),
        Some(Switcher(SwitchType.AttractivenessForSellers(0.005), 20)),
        Some(Switcher(SwitchType.AttractivenessForBuyers(0.005), 20)),
        Some(Switcher(SwitchType.ReinvestmentDelay(3), 20))
      )
    
    val dynamics = switchers map : s =>
      println("Run " + s.map(_.switchType.toString).getOrElse("Base"))
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
            "Reinvestment delay"
          )
        (headers zip metrics.map(_.map(_.toString))).map((h, t) => h +: t).transpose.map(_.mkString(",")).mkString("\n")

      file.overwrite(content)
}
