library(ggplot2)
library(scales)
library(dplyr)
library(stringr)

buildImage = function(dynamicName, dirPath) {
  fifi = paste0(dirPath, dynamicName,".csv")
  df = read.csv(fifi, header = T, sep=",")
  i = 1
  
  traceColumn = function(i){
    dyn = df[,i]
    type = rep(names(df)[i],times = length(dyn))
    trace = t(rbind(type, dyn,1:length(dyn))) %>% data.frame
    return(trace)
  }
  
  traceList = lapply(1:ncol(df), traceColumn)
  all = do.call(rbind, traceList) 
  names(all)=c("variable","value", "years")
  all$value = as.numeric(all$value) 
  all$years = as.numeric(all$years)
  
  ploplot = ggplot(all, aes(x=years))+
    geom_line( aes(y=value, color=variable), linewidth = 0.6)+
    theme_light()+
    xlab("Years")+
    ylab(dynamicName)+
    geom_vline(xintercept=20, linetype='dotted', col = 'black')
  
  pngFileName = str_replace(fifi,".csv",".png")
  ggsave(pngFileName, ploplot, width=1500, height = 891, units = "px", dpi=150)
}

dynamicNames = c("Sales","CO2")
lapply(dynamicNames, buildImage, "/tmp/vinted/")

