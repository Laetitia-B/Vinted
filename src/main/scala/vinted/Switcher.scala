package vinted

import Constants.*

enum SwitchType:
  case AverageDistance(d: KM) extends SwitchType
  case TransportCO2Intensity(tco2: strategy.TransportCO2Intensity) extends SwitchType
  case MaximumItemsBySeller(i: NB_ITEMS_BY_MONTH) extends SwitchType
  case MaximumItemsByBuyer(i: NB_ITEMS_BY_MONTH) extends SwitchType
  case ReplacementRatio(r: Double) extends SwitchType
  case ImpulsiveRatio(r: Double) extends SwitchType
  case SecondHandItemsRatio(r: Double) extends SwitchType
  case ReinvestementInNewRatio(r: Double) extends SwitchType
  case ReinvestementInPlatformRatio(r: Double) extends SwitchType
  case AttractivenessForSellers(r: Double) extends SwitchType
  case AttractivenessForBuyers(a: Double) extends SwitchType
  case ReinvestmentDelay(d: Int) extends SwitchType

import SwitchType.*
import vinted.Constants.NB_ITEMS_BY_MONTH

case class Switcher(switchType: SwitchType, time: Int)

implicit class SimulationStateWrapper(simulation: Simulation):

  def enventuallySwitch(switcher: Switcher, simulationTime: Int): Simulation =
    if (switcher.time == simulationTime)
    then
      switcher.switchType match
        case AverageDistance(d) => simulation.copy(averageDistance = d)
        case TransportCO2Intensity(tco2) => simulation.copy(transportCO2Intensity = tco2)
        case MaximumItemsBySeller(i) => simulation.copy()
        case MaximumItemsByBuyer(i) => simulation.copy()
        case ReplacementRatio(r) => simulation.copy(replacementRatio = r)
        case ImpulsiveRatio(r) => simulation.copy(impulsiveRatio = r)
        case SecondHandItemsRatio(r) => simulation.copy(secondHandItemsRatio = r)
        case ReinvestementInNewRatio(r) => simulation.copy(reinvestementInNewRatio = r)
        case ReinvestementInPlatformRatio(r) => simulation.copy(reinvestementInPlatformRatio = r)
        case AttractivenessForSellers(a) => simulation.copy(attractivenessForSellers = a)
        case AttractivenessForBuyers(a) => simulation.copy(attractivenessForBuyers = a)
        case ReinvestmentDelay(d) => simulation.copy(reinvestmentDelay = d)
    else simulation
