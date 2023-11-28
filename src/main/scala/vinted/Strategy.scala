package vinted


enum TransportCO2Intensity:
  case ReverseSigmoide, DecreasingExponential, Constant


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

package object strategy {

  def getTransportCO2(t: Double, transportCO2Intensity: TransportCO2Intensity) =
    transportCO2Intensity match
      case TransportCO2Intensity.ReverseSigmoide=>  1.0 / (1.0 + Math.exp(t))
      case TransportCO2Intensity.DecreasingExponential=> Math.exp(-t)
      case TransportCO2Intensity.Constant=> 0.01
}
