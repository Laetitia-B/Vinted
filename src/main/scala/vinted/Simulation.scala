package vinted

import Constants.*

type States = Array[MonthlyState]


case class Simulation(
                       initialSellers: Int,
                       initialBuyers: Int,
                       initialItemsBySeller: NB_ITEMS_BY_MONTH,
                       initialItemsByBuyer: NB_ITEMS_BY_MONTH,
                       transportCO2Intensity: TransportCO2Intensity,
                       averageDistance: KM,
                       averagePrice: EURO,
                       replacementRatio: Double,
                       impulsiveRatio: Double,
                       reinvestementInNew: Double, //sum of reinvestments is 1
                       reinvestementInPlatform: Double,
                       reinvestementElsewhere: Double,
                       effectiveSalesCoefficient: Double,
                       attractivenessForSellersDelay: MONTH,
                       attractivenessForBuyersDelay: MONTH,
                       reinvestmentDelay: MONTH
                     )


object Simulation:

  def getMonthlyStateWithDelay(states: States, delay: Int) = states.lift(states.size - delay)

  def run(simulation: Simulation) =

    def iterate(states: States): States = {
      if (states.size == 100) states
      else
        val currentMonthState = states.last
        val stateBeforeAttractivenessForSellersDelay = getMonthlyStateWithDelay(states, simulation.attractivenessForSellersDelay)
        val stateBeforeAttractivenessForBuyersDelay = getMonthlyStateWithDelay(states, simulation.attractivenessForBuyersDelay)


        val totalSellers = currentMonthState.sellers + stateBeforeAttractivenessForSellersDelay.map(_.sales).getOrElse(0.0) * ATTRACTIVENESS_FOR_SELLERS
        val totalBuyers = currentMonthState.buyers + stateBeforeAttractivenessForBuyersDelay.map(_.itemsForSales).getOrElse(0.0) * ATTRACTIVENESS_FOR_BUYERS

        val purchaseIntention = currentMonthState.buyers * simulation.itemsByBuyer

        val sales = Math.min(itemsForSale, purchaseIntention) * simulation.effectiveSalesCoefficient


        val monthlyState = MonthlyState(
          sellers = totalSellers,
          buyers = totalBuyers,
          itemsBySeller = simulation.initialItemsBySeller, // TODO: add a strategy for seller incentive
          itemsByBuyer = simulation.initialItemsByBuyer, // TODO: add a strategy for buyes incentive
          itemsForSales = totalSellers * currentMonthState.itemsBySeller,
          purchaseIntention = purchaseIntention,
          sales = sales,


        )
      iterate(states :+ monthlyState)
    }

//iterate()
