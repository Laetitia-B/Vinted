package vinted


object Run extends App:
  println("Vinted !")

  val simulation =
    Simulation(
      initialSellers = 30,
      initialBuyers = 15,
      initialItemsBySeller = 1.66,
      initialItemsByBuyer = 1.0,
      transportCO2Intensity = TransportCO2Intensity.Constant,
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

  MonthlyState.print(Simulation.run(simulation).last)
