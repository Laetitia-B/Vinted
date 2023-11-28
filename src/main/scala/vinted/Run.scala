package vinted


object Run extends App:
  println("Vinted !")

  val simulation = Simulation(
  initialSellers = 30000000,
  initialBuyers = 15000000,
  initialItemsBySeller = 1.66,
  initialItemsByBuyer = 1.0,
  transportCO2Intensity = TransportCO2Intensity.ReverseSigmoide,
  averageDistance = 150,
  averagePrice = 20,
  replacementRatio = 0.39, //sum of replacement/impulsive < 1
  impulsiveRatio = 0.3,
  secondHandItemsRatio = 0.72, // sum of secondHand and newItems is 1.0
  newItemsRatio = 0.28,
  reinvestementInNewRatio = 0.09, //sum of reinvestments is 1
  reinvestementInPlatformRatio = 0.61,
  reinvestementElsewhereRatio = 0.3,
  effectiveSalesCoefficient = 0.8,
  attractivenessForSellersDelay = 4,
  attractivenessForBuyersDelay = 3,
  reinvestmentDelay = 6
  )

  Simulation.run(simulation)
