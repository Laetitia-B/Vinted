package vinted

import Constants.*

type States = Array[MonthlyState]


case class Simulation(
                       initialSellers: Int,
                       initialBuyers: Int,
                       initialItemsBySeller: Double,
                       initialItemsByBuyer: Double,
                       transportCO2Intensity: TransportCO2Intensity,
                       averageDistance: KM,
                       averagePrice: EURO,
                       replacementRatio: Double, //sum of replacement/impulsive < 1
                       impulsiveRatio: Double,
                       secondHandItemsRatio: Double,
                       newItemsRatio: Double,
                       reinvestementInNewRatio: Double, //sum of reinvestments is 1
                       reinvestementInPlatformRatio: Double,
                       reinvestementElsewhereRatio: Double,
                       effectiveSalesCoefficient: Double,
                       attractivenessForSellersDelay: MONTH,
                       attractivenessForBuyersDelay: MONTH,
                       reinvestmentDelay: MONTH
                     )


object Simulation:

  def getMonthlyStateWithDelay(states: States, delay: Int) = states.lift(states.size - delay)

  def run(simulation: Simulation) =

    def iterate(states: States): States = {
      val timeStep = states.size + 1
      if (timeStep == 100) states
      else
        val currentMonthState = states.last
        val stateBeforeAttractivenessForSellersDelay = getMonthlyStateWithDelay(states, simulation.attractivenessForSellersDelay)
        val stateBeforeAttractivenessForBuyersDelay = getMonthlyStateWithDelay(states, simulation.attractivenessForBuyersDelay)

        val totalSellers = (currentMonthState.sellers + stateBeforeAttractivenessForSellersDelay.map(_.sales).getOrElse(0) * ATTRACTIVENESS_FOR_SELLERS).toInt
        val totalBuyers = (currentMonthState.buyers + stateBeforeAttractivenessForBuyersDelay.map(_.itemsForSale).getOrElse(0) * ATTRACTIVENESS_FOR_BUYERS).toInt

        val additionalPurchaseIntention = getMonthlyStateWithDelay(states, simulation.reinvestmentDelay).map(_.sales).getOrElse(0) * simulation.reinvestementInPlatformRatio

        val itemsByBuyer = simulation.initialItemsByBuyer // TODO: add a strategy for buyer incentive
        val purchaseIntention = (currentMonthState.buyers * itemsByBuyer + additionalPurchaseIntention).toInt

        val itemsForSale = (totalSellers * currentMonthState.itemsBySeller).toInt
        val sales = (Math.min(itemsForSale, purchaseIntention) * simulation.effectiveSalesCoefficient).toInt
        val replacement = (sales * simulation.replacementRatio).toInt
        val income = sales * simulation.averagePrice
        val impulsive = (sales * simulation.impulsiveRatio).toInt
        val totalDistance = sales * simulation.averageDistance
        val transportationEmission = (totalDistance * strategy.getTransportCO2(timeStep, simulation.transportCO2Intensity)).toInt
        val avoidedProductionEmission = (replacement * simulation.secondHandItemsRatio * KG_CO2_PER_ITEM_PRODUCTION).toInt
        val additionalProductionEmission = (impulsive * simulation.newItemsRatio * KG_CO2_PER_ITEM_PRODUCTION).toInt
        val co2reinvestementElsewhere = (income * simulation.reinvestementElsewhereRatio * KG_CO2_PER_EURO_SPENT).toInt
        val co2reinvestementInNew = (sales * simulation.reinvestementInNewRatio * KG_CO2_PER_ITEM_PRODUCTION).toInt

        val monthlyState = MonthlyState(
          sellers = totalSellers,
          buyers = totalBuyers,
          itemsBySeller = simulation.initialItemsBySeller, // TODO: add a strategy for seller incentive
          itemsByBuyer = itemsByBuyer,
          itemsForSale = itemsForSale,
          purchaseIntention = purchaseIntention,
          sales = sales,
          income = income,
          replacement = replacement,
          impulsive = impulsive,
          avoidedProductionEmission = avoidedProductionEmission,
          additionalProductionEmission = additionalProductionEmission,
          co2reinvestementElsewhere = co2reinvestementElsewhere,
          co2reinvestementInNew = co2reinvestementInNew,
          totalDistance = totalDistance,
          transportationEmission = transportationEmission,
          co2Emission = transportationEmission + additionalProductionEmission - avoidedProductionEmission + co2reinvestementElsewhere + co2reinvestementInNew
        )
        iterate(states :+ monthlyState)
    }

//iterate()
