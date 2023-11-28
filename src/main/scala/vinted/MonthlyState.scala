package vinted

import vinted.Constants._

case class MonthlyState(
                         sellers: Int,
                         buyers: Int,
                         itemsBySeller: Double,
                         itemsByBuyer: Double,
                         itemsForSale: NB_ITEMS_BY_MONTH,
                         purchaseIntention: NB_ITEMS_BY_MONTH,
                         sales: NB_ITEMS_BY_MONTH,
                         income: EURO,
                         replacement: Int,
                         impulsive: Int,
                         co2reinvestementInNew: KG_CO2,
                         co2reinvestementElsewhere: KG_CO2, 
                         avoidedProductionEmission: KG_CO2,
                         additionalProductionEmission: KG_CO2,
                         totalDistance: KM,
                         transportationEmission: KG_CO2,
                         co2Emission: KG_CO2
                 )