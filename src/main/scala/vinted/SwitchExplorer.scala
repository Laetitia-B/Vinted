package vinted

import vinted.Constants.NB_ITEMS_BY_MONTH
import vinted.Switcher.*
import better.files.File

object SwitchExplorer {

  def explore(outputPath: String) =

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

    val initialSimulation =
      Simulation(
        initialSellers = 30,
        initialBuyers = 15,
        initialItemsBySeller = 1.66,
        initialItemsByBuyer = 1.0,
        maximumItemsBySeller = 2.0,
        maximumItemsBySellerDelay = 50,
        maximumItemsByBuyer = 1.0,
        maximumItemsByBuyerDelay = 50,
        transportCO2Intensity = strategy.TransportCO2Intensity.Constant(0.01),
        averageDistance = 150,
        averagePrice = 20,
        replacementRatio = 0.39, //sum of replacement/impulsive < 1
        impulsiveRatio = 0.3,
        secondHandItemsRatio = 0.72, // sum of secondHand and newItems is 1.0
        reinvestementInNewRatio = 0.09, //sum of reinvestments is 1
        reinvestementInPlatformRatio = 0.61,
        effectiveSalesCoefficient = 0.85,
        attractivenessForSellersDelay = 4,
        attractivenessForBuyersDelay = 3,
        attractivenessForSellers = 0.01,
        attractivenessForBuyers = 0.01,
        reinvestmentDelay = 6
      )

    val dynamics = switchers map : s =>
      println("Run " + s.map(_.switchType.toString).getOrElse("Base"))
      val states = Simulation.run(initialSimulation, s)

      Seq(
        states.map(_.sales),
        states.map(_.co2Emission)
      )

    val transposed = dynamics.transpose

    val dir = File(outputPath)
    better.files.Dsl.mkdir(dir)

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
