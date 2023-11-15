package vinted

import vinted.Constants.{ATTRACTIVENESS_FOR_BUYERS, ATTRACTIVENESS_FOR_SELLERS, EURO, KM}

type States = Array[MonthlyState]
case class Reinvestment(`new`: Double, platform: Double, elsewhere: Double) // sum is 1


case class Simulation(
      initialSellers: Int,
      initialBuyers: Int,
      itemsBySellerByMonth: Double,
      itemsByBuyerByMonth: Double,
      transportCO2Intensity: TransportCO2Intensity,
      averageDistance: KM,
      averagePrice: EURO,
      replacementRatio: Double,
      impulsiveRatio: Double,
      reinvestment: Reinvestment,
      effectiveSalesCoefficient: Double

  )

object Simulation:

  def run(simulation: Simulation) =

    def iterate(states: States): States = {
      if (states.size == 100) states
      else
        val previousMonthState = states.last

        val itemsForSale = previousMonthState.sellers * simulation.itemsBySellerByMonth
        val purchaseIntention = previousMonthState.buyers * simulation.itemsByBuyerByMonth

        val sales = Math.min(itemsForSale, purchaseIntention) * simulation.effectiveSalesCoefficient

        val totalSellers = previousMonthState.sellers + sales * ATTRACTIVENESS_FOR_SELLERS
        val totalBuyers = previousMonthState.buyers + itemsForSale * ATTRACTIVENESS_FOR_BUYERS

      val monthlyState = MonthlyState(
          sellers = previousMonthState.sellers + 0.01 * sales


        )
        iterate(states :+ monthlyState)
    }

    //iterate()
