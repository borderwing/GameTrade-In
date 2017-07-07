from bs4 import BeautifulSoup
import urllib.request


def getEvaluatePoint(gameName):
    url = 'https://www.amazon.com/s/ref=nb_sb_noss_2?url=search-alias%3Daps&field-keywords='+gameName

    user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
    headers = {'User-Agent': user_agent}


    req=urllib.request.Request(url, headers=headers)

    myResponse = urllib.request.urlopen(req)
    myPage = myResponse.read()

    soup = BeautifulSoup(myPage, "html.parser")

    rightContent = soup.findAll('span', attrs={"class": "a-size-base a-color-base"})
    sum = 0
    cnt = 1
    for price in rightContent:
        priceDetail = price.get_text()
        priceDetail = priceDetail[1:]
        sum = sum+float(priceDetail)
        if(cnt == 5):
            break
        cnt += 1

    return(sum/cnt)
