package vinted

import Constants.*
import Switcher.*

type States = Array[MonthlyState]


case class Simulation(
                       initialSellers: Int,
                       initialBuyers: Int,
                       initialItemsBySeller: NB_ITEMS_BY_MONTH,
                       initialItemsByBuyer: NB_ITEMS_BY_MONTH,
                       maximumItemsBySeller: NB_ITEMS_BY_MONTH,
                       maximumItemsByBuyer: NB_ITEMS_BY_MONTH,
                       maximumItemsBySellerDelay: MONTH,
                       maximumItemsByBuyerDelay: MONTH,
                       transportCO2Intensity: strategy.TransportCO2Intensity,
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

  def applySwitcher(switcher: Option[Switcher], simulation: Simulation, step: Int): Simulation =
    switcher match
      case Some(s)=>  simulation.enventuallySwitch(s, step)
      case None=> simulation
      
  def run(simulation: Simulation, switcher: Option[Switcher] = None): States =

    def iterate(states: States, simulation: Simulation): States =
      val timeStep = states.size + 1
      if (timeStep == 100) states
      else
        val simulationState = applySwitcher(switcher, simulation, timeStep)
        val currentMonthState = states.last
        val stateBeforeAttractivenessForSellersDelay = getMonthlyStateWithDelay(states, simulationState.attractivenessForSellersDelay)
        val stateBeforeAttractivenessForBuyersDelay = getMonthlyStateWithDelay(states, simulationState.attractivenessForBuyersDelay)

        val totalSellers = simulationState.initialSellers + stateBeforeAttractivenessForSellersDelay.map(_.sales).getOrElse(0.0) * simulationState.attractivenessForSellers
        val totalBuyers = simulationState.initialBuyers + stateBeforeAttractivenessForBuyersDelay.map(_.itemsForSale).getOrElse(0.0) * simulationState.attractivenessForBuyers

        val additionalPurchaseIntention = getMonthlyStateWithDelay(states, simulationState.reinvestmentDelay).map(_.sales).getOrElse(0.0) * simulationState.reinvestementInPlatformRatio

        val itemsByBuyer = 
          if (simulationState.maximumItemsByBuyerDelay - timeStep < 0 ) math.min(simulationState.maximumItemsByBuyer, simulationState.initialItemsByBuyer)
          else simulationState.initialItemsByBuyer
        val purchaseIntention = currentMonthState.buyers * itemsByBuyer + additionalPurchaseIntention

        val itemsBySeller = 
          if (simulationState.maximumItemsBySellerDelay - timeStep < 0) math.min(simulationState.maximumItemsBySeller, simulationState.initialItemsBySeller)
          else simulationState.initialItemsBySeller
        
        val itemsForSale = totalSellers * currentMonthState.itemsBySeller
        val sales = Math.min(itemsForSale, purchaseIntention) * simulationState.effectiveSalesCoefficient
        val replacement = sales * simulationState.replacementRatio
        val income = sales * simulationState.averagePrice
        val impulsive = sales * simulationState.impulsiveRatio
        val totalDistance = sales * simulationState.averageDistance
        val transportationEmission = totalDistance * strategy.getTransportCO2(timeStep, simulationState.transportCO2Intensity)
        val avoidedProductionEmission = replacement * simulationState.secondHandItemsRatio * KG_CO2_PER_ITEM_PRODUCTION
        val additionalProductionEmission = impulsive * (1 - simulationState.secondHandItemsRatio) * KG_CO2_PER_ITEM_PRODUCTION
        val co2reinvestementElsewhere = income * (1 - simulationState.reinvestementInNewRatio - simulationState.reinvestementInPlatformRatio) * KG_CO2_PER_EURO_SPENT
        val co2reinvestementInNew = sales * simulationState.reinvestementInNewRatio * KG_CO2_PER_ITEM_PRODUCTION

        val monthlyState = MonthlyState(
          sellers = totalSellers,
          buyers = totalBuyers,
          itemsBySeller = itemsBySeller, // TODO: add a strategy for seller incentive
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
        iterate(states :+ monthlyState, simulationState)

    val firstMonthState =
      MonthlyState(
        sellers = simulation.initialSellers,
        buyers = simulation.initialBuyers,
        itemsBySeller = simulation.initialItemsBySeller,
        itemsByBuyer = simulation.initialItemsByBuyer,
        itemsForSale = (simulation.initialSellers * simulation.initialItemsBySeller + simulation.initialBuyers * simulation.initialItemsByBuyer).toInt,
        purchaseIntention = (simulation.initialBuyers * simulation.initialItemsByBuyer).toInt
      )


    iterate(Array(firstMonthState), simulation)
