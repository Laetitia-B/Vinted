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
                       replacementRatio: Double, //sum of replacement/impulsive < 1
                       impulsiveRatio: Double,
                       secondHandItemsRatio: Double,
                       reinvestementInNewRatio: Double, //sum of reinvestments is 1
                       reinvestementInPlatformRatio: Double,
                       effectiveSalesCoefficient: Double,
                       attractivenessForSellersDelay: MONTH,
                       attractivenessForBuyersDelay: MONTH,
                       attractivenessForSellers: Double,
                       attractivenessForBuyers: Double,
                       reinvestmentDelay: MONTH
                     )


object Simulation:

  def getMonthlyStateWithDelay(states: States, delay: Int) = states.lift(states.size - delay)

  def run(simulation: Simulation): States =

    def iterate(states: States): States = {
      val timeStep = states.size + 1
      if (timeStep == 100) states
      else
        val currentMonthState = states.last
        val stateBeforeAttractivenessForSellersDelay = getMonthlyStateWithDelay(states, simulation.attractivenessForSellersDelay)
        val stateBeforeAttractivenessForBuyersDelay = getMonthlyStateWithDelay(states, simulation.attractivenessForBuyersDelay)

        val totalSellers = simulation.initialSellers + stateBeforeAttractivenessForSellersDelay.map(_.sales).getOrElse(0.0) * simulation.attractivenessForSellers
        val totalBuyers = simulation.initialBuyers + stateBeforeAttractivenessForBuyersDelay.map(_.itemsForSale).getOrElse(0.0) * simulation.attractivenessForBuyers

        val additionalPurchaseIntention = getMonthlyStateWithDelay(states, simulation.reinvestmentDelay).map(_.sales).getOrElse(0.0) * simulation.reinvestementInPlatformRatio

        val itemsByBuyer = simulation.initialItemsByBuyer // TODO: add a strategy for buyer incentive
        val purchaseIntention = currentMonthState.buyers * itemsByBuyer + additionalPurchaseIntention

        val itemsForSale = totalSellers * currentMonthState.itemsBySeller
        val sales = Math.min(itemsForSale, purchaseIntention) * simulation.effectiveSalesCoefficient
        val replacement = sales * simulation.replacementRatio
        val income = sales * simulation.averagePrice
        val impulsive = sales * simulation.impulsiveRatio
        val totalDistance = sales * simulation.averageDistance
        val transportationEmission = totalDistance * strategy.getTransportCO2(timeStep, simulation.transportCO2Intensity)
        val avoidedProductionEmission = replacement * simulation.secondHandItemsRatio * KG_CO2_PER_ITEM_PRODUCTION
        val additionalProductionEmission = impulsive * (1 - simulation.secondHandItemsRatio) * KG_CO2_PER_ITEM_PRODUCTION
        val co2reinvestementElsewhere = income * (1 - simulation.reinvestementInNewRatio - simulation.reinvestementInPlatformRatio) * KG_CO2_PER_EURO_SPENT
        val co2reinvestementInNew = sales * simulation.reinvestementInNewRatio * KG_CO2_PER_ITEM_PRODUCTION

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

    val firstMonthState =
      MonthlyState(
        sellers = simulation.initialSellers,
        buyers = simulation.initialBuyers,
        itemsBySeller = simulation.initialItemsBySeller,
        itemsByBuyer = simulation.initialItemsByBuyer,
        itemsForSale = (simulation.initialSellers * simulation.initialItemsBySeller + simulation.initialBuyers * simulation.initialItemsByBuyer).toInt,
        purchaseIntention = (simulation.initialBuyers * simulation.initialItemsByBuyer).toInt
      )


    iterate(Array(firstMonthState))
