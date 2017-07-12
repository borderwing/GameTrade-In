from bs4 import BeautifulSoup
import urllib2
from selenium import webdriver

driver = webdriver.PhantomJS(executable_path='F:\python2\Scripts\phantomjs-2.1.1-windows\phantomjs.exe')
driver.get("https://www.g2a.com/?search=dark+souls+3")

soup = BeautifulSoup(driver.page_source, "html.parser")
rightContent=soup.findAll('strong',attrs={'class':'mp-pi-price-min'})
print(11)
for price in rightContent:
    for parent in price.parents:
        if parent['class']=="mp-product-info":
            print (price)
