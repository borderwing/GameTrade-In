from bs4 import BeautifulSoup
import urllib2
from selenium import webdriver

def GetEvaluatePointsByG2A(gameName):

    gameName=gameName.replace(" ", "+")

    print(gameName)

    #driver = webdriver.PhantomJS(executable_path='F:\python2\Scripts\phantomjs-2.1.1-windows\phantomjs.exe')
    driver = webdriver.PhantomJS(executable_path='F:\\python2\\Scripts\\phantomjs-2.1.1-windows\\phantomjs.exe')
    url = "https://www.g2a.com/?search="+gameName
    print(url)
    driver.get(url)

    soup = BeautifulSoup(driver.page_source, "html.parser")
    rightContent = soup.findAll('strong',attrs={'class':'mp-pi-price-min'})
    price=rightContent[-1].get_text()[0:-2]
    result = (int)(float(price)*100)
    if(result==5456):
        return 0
    else:
        return (int(float(price)*100))

