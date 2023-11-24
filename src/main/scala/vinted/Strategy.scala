package vinted

enum TransportCO2Intensity:
  case Sigmoide, DecreasingExponential


case class Reinvestment(`new`: Double, platform: Double, elsewhere: Double) // sum is 1

type Delay = Int
type SimulationStep = Int
//type ReinvestmentDistributionChange = Reinvestment => Reinvestment
//type ReinvestmentDistributionPolicy = Array[(SimulationStep, ReinvestmentDistributionChange)]
//
//def fivePcToNew(reinvestment: Reinvestment) = Reinvestment(`new` = reinvestment.`new` * 0.95, platform = reinvestment.platform * 1.05)
//
//val rdp = Array(
//  (10, (r: Reinvestment)=> fivePcToNew(r)),
//  (20, (r: Reinvestment)=> fivePcToNew(r))
//)
