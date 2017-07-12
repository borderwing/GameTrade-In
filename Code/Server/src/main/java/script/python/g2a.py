from bs4 import BeautifulSoup
import urllib2
from selenium import webdriver


def getEvaluatePoints(gameName):
    gameName=gameName.replace("", "+")

    driver = webdriver.PhantomJS(executable_path='F:\python2\Scripts\phantomjs-2.1.1-windows\phantomjs.exe')
    driver.get("https://www.g2a.com/?search="+gameName)

    soup = BeautifulSoup(driver.page_source, "html.parser")
    rightContent=soup.findAll('strong',attrs={'class':'mp-pi-price-min'})

    priceDetail=rightContent[-1].get_text()
    return (float(priceDetail[0:-2]))