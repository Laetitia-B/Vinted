package vinted

object Metrics {

  def totalCO2(states: States) =
    states.map(_.co2Emission).sum
    
  def totalSales(states: States) =
    states.map(_.sales).sum
}
