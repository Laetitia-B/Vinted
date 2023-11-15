package vinted

import vinted.Constants.{EURO, KM}

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

  )

object Simulation:

  def iterate(states: States): States = {
    if (states.size == 100) states
    else
      val previousMonthState = states.last
      previousMonthState.sel

      val monthlyState = MonthlyState(

      )
      iterate(states :+ monthlyState)
  }
