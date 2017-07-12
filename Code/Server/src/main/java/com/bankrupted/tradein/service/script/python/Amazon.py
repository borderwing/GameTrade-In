from bs4 import BeautifulSoup
import urllib2


def getEvaluatePoint(gameName,platform):

    #get the url by platform

    url="";

    gameName=gameName.replace(" ","+")

    if(platform=="PC"):
        url = 'https://www.amazon.com/s/ref=nb_sb_noss?url=node%3D4924894011&field-keywords='+gameName
    elif(platform=="PS4"):
        url = 'https://www.amazon.com/s/ref=nb_sb_noss_2?url=node%3D6427814011&field-keywords='+gameName
    elif(platform == "XBOX ONE"):
        url='https://www.amazon.com/s/ref=nb_sb_noss_2?url=node%3D6469269011&field-keywords='+gameName
    elif(platform == ""):
        url='https://www.amazon.com/s/ref=nb_sb_noss?url=node%3D16227128011&field-keywords='+gameName
    elif(platform=="Wii U"):
        url='https://www.amazon.com/s/ref=nb_sb_noss_2?url=node%3D3075112011&field-keywords='+gameName
    elif(platform=="3DS"):
        url='https://www.amazon.com/s/ref=nb_sb_noss?url=node%3D2622269011&field-keywords='+gameName
    elif(platform=="PS3"):
        url='https://www.amazon.com/s/ref=nb_sb_noss?url=node%3D14210751&field-keywords='+gameName
    elif(platform=="XBOX 360"):
        url='https://www.amazon.com/s/ref=nb_sb_noss?url=node%3D14220161&field-keywords='+gameName



    user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
    headers = {'User-Agent': user_agent}

    req = urllib2.Request(url, headers=headers)

    myResponse = urllib2.urlopen(req)
    myPage=myResponse.read()
    #, "class": "a-size-base a-color-base"
    soup = BeautifulSoup(myPage,"html.parser")
    rightContent = soup.findAll('span',attrs={"class":"a-size-base a-color-base"})
    sum = 0
    cnt = 1
    for price in rightContent:
        priceDetail = price.get_text()
        priceDetail = priceDetail[1:]
        print(priceDetail)
        sum = sum+float(priceDetail)
        if(cnt == 5):
            break
        cnt += 1

    return(sum/cnt)

