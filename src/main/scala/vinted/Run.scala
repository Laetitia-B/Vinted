package vinted


object Run extends App:
  println("Vinted !")

  Simulation
    (

      transportCO2Intensity = TransportCO2Intensity.Sigmoide,

    )

