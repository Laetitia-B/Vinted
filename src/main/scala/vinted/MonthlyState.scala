package vinted

import vinted.Constants._

case class MonthlyState(
                         sellers: Int,
                         buyers: Int,
                         sales: NB_ITEMS,
                         income: EURO,
                         replacement: Int,
                         impulsive: Int,
                         avoidedProductionEmission: KG_CO2,
                         additionalProductionEmission: KG_CO2,
                         totalDistance: KM,
                         transportationEmission: KG_CO2,
                         co2Emission: KG_CO2
                 )

