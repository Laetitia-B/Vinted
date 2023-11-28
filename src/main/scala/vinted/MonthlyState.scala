package vinted

import vinted.Constants._

case class MonthlyState(
                         sellers: Double,
                         buyers: Double,
                         itemsBySeller: Double,
                         itemsByBuyer: Double,
                         itemsForSale: NB_ITEMS_BY_MONTH,
                         purchaseIntention: NB_ITEMS_BY_MONTH,
                         sales: NB_ITEMS_BY_MONTH = 0,
                         income: EURO = 0,
                         replacement: Double = 0.0,
                         impulsive: Double = 0.0,
                         co2reinvestementInNew: KG_CO2 = 0.0,
                         co2reinvestementElsewhere: KG_CO2 = 0.0,
                         avoidedProductionEmission: KG_CO2 = 0.0,
                         additionalProductionEmission: KG_CO2 = 0.0,
                         totalDistance: KM = 0.0,
                         transportationEmission: KG_CO2 = 0.0,
                         co2Emission: KG_CO2 = 0.0
                       )

object MonthlyState {
  def print(ms: MonthlyState) =
    val doubleFormat = "%.0f"
    val locale = new java.util.Locale("en", "EN")
    def pretify(d: Double) = doubleFormat.formatLocal(locale, d)

    println(
      s"""
      SELLERS               : ${pretify(ms.sellers)}
      BUYERS                : ${pretify(ms.buyers)}
      ITEMS_BY_SELLER       : ${ms.itemsBySeller}
      ITEMS_BY_BUYER        : ${ms.itemsByBuyer}
      ITEMS_FOR_SALE        : ${pretify(ms.itemsForSale)}
      PURCHASE_INTENSION    : ${pretify(ms.purchaseIntention)}
      SALES                 : ${pretify(ms.sales)}
      INCOME                : ${pretify(ms.income)}
      REPLACEMENT           : ${pretify(ms.replacement)}
      IMPULSIVE             : ${pretify(ms.impulsive)}
      CO2_REINVESTMENT_NEW  : ${pretify(ms.co2reinvestementInNew)}
      CO2_REINVESTMENT_ELSE : ${pretify(ms.co2reinvestementElsewhere)}
      AVOIDED_PRODUCTON     : ${pretify(ms.avoidedProductionEmission)}
      ADDITIONAL_PRODUCTION : ${pretify(ms.additionalProductionEmission)}
      TOTAL_DISTANCE        : ${pretify(ms.totalDistance)}
      TRANSPORT_EMMISSION   : ${pretify(ms.transportationEmission)}
      CO2_EMISSION          : ${pretify(ms.co2Emission)}
      """
    )

}