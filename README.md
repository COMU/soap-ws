callws Kullanım Dökümanı
=====================


callws web servisleri hakkında bilgi edinmenizi ve kullanmanızı sağlayan bir komut satırı uygulamasıdır.

----------
Bağımlılıklar ve Derleme
---------
soap-ws kütüphanesi ve callws uygulaması bağımlılık yönetimi için [Maven](http://maven.apache.org/) kullanmaktadır. Projeyi git deposundan aldıktan sonra `mvn eclipse:eclipse` hedefini çalıştırarak bağımlılıklarla beraber bir Eclipse çalışma ortamı hazırlayabilirsiniz. 

`mvn install` hedefi ise projeyi derleyip paketlemektedir, derlenmiş çalıştırılabilir *.jar dosyası `soap-ws/callws/target` içerisinde oluşmaktadır, bu dosyanın bağımlılıkları ise yine aynı klasördeki `lib` klasörü içerisine atılmaktadır. 

Parametreler
---------

> usage: callws [-b (arg)] [-c] [-e (arg)] [-h] [-l] [-lp] [-o (arg)] [-p (arg)] [-w (arg)]
>  **-b, --binding (arg)**
>  **-c, --check-endpoint**  
>  **-e, --endpoint (arg) ** 
>  **-h, --help**
>  **-l, --list-operations**
>  **-lp, --list-parameters**
>  **-o, --operation (arg)**
>  **-p, --parameters (arg)** 
>  **-w, --wsdl (arg)**

--------
#### Web servisinin geçerli ve erişilebilir olduğunu kontrol etmek

Örnek girdi: 
> **java -jar callws.jar --wsdl** http://www.webservicex.com/CurrencyConvertor.asmx?wsdl **--check-endpoint**

Örnek çıktı: 
> "http://www.webservicex.com/CurrencyConvertor.asmx?wsdl" is accessible and well formed.

-------
#### Servisin fonksiyonlarının listelenmesi

Örnek girdi: 
> **java -jar callws.jar --wsdl** http://www.webservicex.com/CurrencyConvertor.asmx?wsdl **--list-operations**

Örnek çıktı: 
> **Binding name:** CurrencyConvertorSoap, **Operation name:** ConversionRate
> **Binding name:** CurrencyConvertorHttpPost, **Operation name:** ConversionRate
> **Binding name:** CurrencyConvertorSoap12, **Operation name:** ConversionRate
> **Binding name:** CurrencyConvertorHttpGet, **Operation name:** ConversionRate

--------
#### Fonksiyona ait parametrelerin listelenmesi

Örnek girdi: 
> **java -jar callws.jar --wsdl** http://www.webservicex.com/CurrencyConvertor.asmx?wsdl **--binding** CurrencyConvertorSoap **--operation** ConversionRate **--list-parameters**

Not: Burada -e/--endpoint parametresi kullanılarak istek aynı wsdl'i kullanan başka bir servise de gönderilebilir.

Örnek çıktı: 
> **Parameter name:** FromCurrency, **Parameter type:** Currency
> **Parameter name:** ToCurrency, **Parameter type:** Currency

--------

#### Servise ait bir fonksiyonun çağrılması

Örnek girdi:
> java -jar callws.jar **--wsdl** "http://www.webservicex.com/CurrencyConvertor.asmx?wsdl" **--binding** CurrencyConvertorSoap **--operation** ConversionRate **--parameters** "{'FromCurrency': 'USD', 'ToCurrency': 'TRY'}"

Örnek çıktı:

<pre>&lt;soap:Envelope xmlns:soap=&quot;http://schemas.xmlsoap.org/soap/envelope/&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xmlns:xsd=&quot;http://www.w3.org/2001/XMLSchema&quot;&gt;
  &lt;soap:Body&gt;
    &lt;ConversionRateResponse xmlns=&quot;http://www.webserviceX.NET/&quot;&gt;
      &lt;ConversionRateResult&gt;<b>2.0939</b>&lt;/ConversionRateResult&gt;
    &lt;/ConversionRateResponse&gt;
  &lt;/soap:Body&gt;
&lt;/soap:Envelope&gt;</pre>

