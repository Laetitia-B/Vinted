package vinted


object Run extends App:
  
  val simulation =
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
    
  switchExplorer
  //default
  
  def default = 
    println("Vinted !")
    
    val states = Simulation.run(simulation)
    MonthlyState.print(states.last)
  
    println("TOTAL CO2 " + Metrics.totalCO2(states))
    println("TOTAL SALES " + Metrics.totalSales(states))

  def switchExplorer =
    println("Switch explorer !")
    SwitchExplorer.explore("/tmp/vinted", simulation)
    
    