public class PaymentServicer_PaypalTransaction {
    public static final String AGENTCODE = 'agentCode';
    public static final String PASSWORD = 'password';

    public static final String CREDIT_CARD_NUMBER = 'creditCardNum';
    public static final String CREDITCARD_EX_MONTH = 'expire_month';
    public static final String CREDITCARD_EX_YEAR = 'expire_year';
    public static final String CREDIT_CARD_CVV2 = 'cvv2';
    public static final String CREDIT_CARD_MOP = 'mop';
    public static final String FIRSTNAME = 'firstName';
    public static final String LASTNAME = 'lastName';
    public static final String FULLNAME = 'name';
    public static final String ADDRESS = 'address';
    public static final String ADDR_CITY = 'city';
    public static final String ADDR_STATE = 'state';
    public static final String ADDR_COUNTRY_CODE = 'country_code';
    public static final String ZIPCODE = 'zipCode';
    public static final String CUSTOM_VAR = 'custom';

    public static final String RESPONSEMESSAGE = 'responseMessage';
    public static final String ISSUCCESS = 'isSuccess';
    public static final String DATE_TIME = 'dateTime';

    public static final String TRANSACTIONID = 'transactionId';
    public static final String TOTAL_AMOUNT = 'total';
    public static final String CURRENCY_CODE = 'currency';
    public static final String CURRENCY_CODE_USD = 'USD';
    public static final String OPPORTUNITY_DESCRIPTION = 'description';
    public static final String PAYERID = 'payerid';

    public static final String MERCHANT_NAME = 'merchant_name';
    public static final String MERCHANT_USER_ID = 'merchant_user_id';
    public static final String MERCHANT_PASS = 'merchant_pass';
    public static final String ENDPOINT = 'endpoint';
    public static final String ENDPOINT_SANDBOX = 'Sandbox';
    public static final String INVOICE_ID = 'invoice id';
    public static final String CONTACT_EMAIL = 'email';
    public class MissingMerchantException extends Exception {}
    public class AuthorizationFailedException extends Exception {}


    public class Transactions {
        public Amount amount;
        public String description;
        public String invoice_number;
        public String custom;
    }

    public class ResponseTransactions {
        public Amount amount;
        public String description;
        public Related_Resource[] related_resources;
    }

    public class Amount {
        public String entered_currency;
        public String total;
        public Details details;
    }

    public class Details {
        public String subtotal;
    }

    public class Related_Resource {
        public Sale sale;
    }

    public class Sale {
        public String id;
        public String create_time;
        public String update_time;
        public String state;
        public Amount amount;
        public String parent_payment;
        public Link[] links;
    }

    public class TokenResponse {
        public String scope;
        public String access_token;
        public String token_type;
        public String app_id;
        public String expires_in;
    }

    public class Link {
        public String href;
        public String rel;
        public String method;
    }

    public class Error {
        public String name;
        public String message;
        public String information_link;
        public String debug_id;
    }

    public class Billing_Address {
        public String line1;
        public String line2;
        public String city;
        public String country_code;
        public String postal_code;
        public String state;
        public String phone;
    }

    public class Credit_Card {
        public String card_number;
        public String type;
        public String expire_month;
        public String expire_year;
        public String cvv2;
        public String first_name;
        public String last_name;
        public Billing_Address billing_address;
    }

    public class Funding_Instrument_Without_Token {
        public Credit_Card credit_card;
    }

    public class Payer_Without_Token {
        public String payment_method;
        public Funding_Instrument_Without_Token[] funding_instruments;
        public payer_info payer_info;
    }

    public class ProcessCCWithoutTokenRequest {
        public String intent;
        public Payer_Without_Token payer;
        public Transactions[] transactions;
    }

    public class payer_info {
        public String email;
    }

    public class ProcessCCWithoutTokenResponse {
        public String id;
        public String create_time;
        public String update_time;
        public String state;
        public String intent;
        public Payer_Without_Token payer;
        public ResponseTransactions[] transactions;
        public Link[] links;
    }


    public static string getAccessToken(String client_id, String secrete, String endPointName){

        String requestBody = 'grant_type=client_credentials';

        Blob headerValue = Blob.valueOf(client_id+ ':' +secrete);
        String authorizationHeader = 'BASIC ' + EncodingUtil.base64Encode(headerValue);

        String payPalEndpointAccessToken;
        if (ENDPOINT_SANDBOX.equalsIgnoreCase(endPointName))
            payPalEndpointAccessToken = PaymentConstants.SANDBOX_ENDPOINTS_MAP_PAYPAL.get(PaymentConstants.PAYPAL_OP_GET_ACCESS_TOKEN);
        else
            payPalEndpointAccessToken = PaymentConstants.PRODUCTION_ENDPOINTS_MAP_PAYPAL.get(PaymentConstants.PAYPAL_OP_GET_ACCESS_TOKEN);

        HttpResponse httpResponse = new Integration().getEndpointResponse(null, payPalEndpointAccessToken, new Map<String, String> {
            'Content-Type'   => 'application/x-www-form-urlencoded',
            'Content-Length' => '' + requestBody.length(),
            'Accept'         => 'application/json',
            'authorization'  =>  authorizationHeader,
            'Accept-Language'=> 'en_US',
            'PayPal-Partner-Attribution-Id' => 'roundCorner_SP'
        }, requestBody, null);

        system.debug('authorization Response body-->'+httpResponse.getBody());
        if (Test.isRunningTest() == true)
            httpResponse = getTokenResponse();

        TokenResponse tokenResponse = (PaymentServicer_PaypalTransaction.TokenResponse) JSON.deserialize(httpResponse.getBody(), PaymentServicer_PaypalTransaction.TokenResponse.class);
        system.debug('tokenResponse-->'+tokenResponse);

        //if (tokenResponse.access_token == Null) {
          //  throw new AuthorizationFailedException();
        //}
        return tokenResponse.token_type +' '+ tokenResponse.access_token;
    }

    public static httpResponse getTokenResponse() {
        HttpResponse httpResponse=new httpResponse();
        httpResponse.setBody('{"scope":"openid https://uri.paypal.com/services/invoicing https://api.paypal.com/v1/payments/.* https://api.paypal.com/v1/vault/credit-card/.* https://api.paypal.com/v1/vault/credit-card","access_token":"A015CrDqkUWDDWAoOWwGmbr2QWDRtzRd2NBEuossivNia3Q","token_type":"Bearer","app_id":"APP-80W284485P519543T","expires_in":28800}');
        return httpResponse;
    }

    public PageReference getEndpoint(Map<String, String> paymentData) {
        String merchantName = paymentData.get(MERCHANT_NAME);
        String merchantUserId = paymentData.get(MERCHANT_USER_ID);
        String merchantPass = paymentData.get(MERCHANT_PASS);
        String endPointName = paymentData.get(ENDPOINT);

        if (merchantUserId == null || merchantUserId == '' ||
            merchantPass == null || merchantPass == '') {
            throw new MissingMerchantException();
        }

        String payPalEndpoint;
        if (ENDPOINT_SANDBOX.equalsIgnoreCase(endPointName)) {
            payPalEndpoint = PaymentConstants.SANDBOX_ENDPOINTS_MAP_PAYPAL.get(PaymentConstants.PAYPAL_OP_PROCESS_CC_WITHOUT_TOKEN);
        } else {
            payPalEndpoint = PaymentConstants.PRODUCTION_ENDPOINTS_MAP_PAYPAL.get(PaymentConstants.PAYPAL_OP_PROCESS_CC_WITHOUT_TOKEN);
        }

        String transactionType = PaymentConstants.PAYPAL_OP_PROCESS_CC_WITHOUT_TOKEN;


        PageReference pageReference = new PageReference(payPalEndpoint);
        pageReference.getParameters().put('op',transactionType);
        pageReference.getParameters().put(AGENTCODE, encodeValue(merchantUserId));
        pageReference.getParameters().put(PASSWORD, encodeValue(merchantPass));
        pageReference.getParameters().put(ENDPOINT, encodeValue(endPointName));

        String month = paymentData.get(CREDITCARD_EX_MONTH);
        String year = paymentData.get(CREDITCARD_EX_YEAR);

        String cardNumber = paymentData.get(CREDIT_CARD_NUMBER);

        pageReference.getParameters().put(CREDIT_CARD_NUMBER, encodeValue(cardNumber));
        pageReference.getParameters().put(CREDITCARD_EX_MONTH, String.valueOf(month));
        pageReference.getParameters().put(CREDITCARD_EX_YEAR, String.valueOf(year));
        pageReference.getParameters().put(CREDIT_CARD_CVV2, encodeValue(paymentData.get(CREDIT_CARD_CVV2)));

        pageReference.getParameters().put(CREDIT_CARD_MOP, encodeValue(getCreditCardType(cardNumber)));

        String full_Name = paymentData.get(FULLNAME);

        String[] nameParts = full_Name != null? full_Name.split(' ',2) : new String[]{};

        pageReference.getParameters().put(FIRSTNAME, encodeValue(nameParts.size() != 0? nameParts[0] : ''));
        pageReference.getParameters().put(LASTNAME, encodeValue(nameParts.size() > 1? nameParts[1] : ' '));
        pageReference.getParameters().put(CUSTOM_VAR, encodeValue(paymentData.get(CUSTOM_VAR)));
        pageReference.getParameters().put(ADDRESS, encodeValue(paymentData.get(ADDRESS)));
        pageReference.getParameters().put(ADDR_CITY, encodeValue(paymentData.get(ADDR_CITY)));
        pageReference.getParameters().put(ADDR_STATE, encodeValue(paymentData.get(ADDR_STATE)));
        pageReference.getParameters().put(ADDR_COUNTRY_CODE, encodeValue(paymentData.get(ADDR_COUNTRY_CODE)));
        pageReference.getParameters().put(ZIPCODE, encodeValue(paymentData.get(ZIPCODE)));

        pageReference.getParameters().put(TOTAL_AMOUNT, encodeValue(paymentData.get(TOTAL_AMOUNT)));
        pageReference.getParameters().put(CURRENCY_CODE, encodeValue(CURRENCY_CODE_USD));
        pageReference.getParameters().put(INVOICE_ID, encodeValue(paymentData.get(INVOICE_ID)));
        pageReference.getParameters().put(CONTACT_EMAIL, paymentData.get(CONTACT_EMAIL));

        return pageReference;
    }

    public HttpResponse getEndpointResponse(PageReference pageReference) {

        String[] pageReferenceParts = pageReference.getUrl().split('\\?', 2);

        String requestBody = '';
        if (PaymentConstants.PAYPAL_OP_PROCESS_CC_WITHOUT_TOKEN.equalsIgnoreCase(blank(pageReference.getParameters().get('op')))) {
            requestBody = getRequest_PROCESS_CC_WITHOUT_TOKEN(pageReference);
        }

        String authorizationToken =  getAccessToken(blank(pageReference.getParameters().get(AGENTCODE)),
                                                    blank(pageReference.getParameters().get(PASSWORD)),
                                                    blank(pageReference.getParameters().get('ENDPOINT'))
                                                   );

        if (authorizationToken == null) {
            Httpresponse httpresponse = new Httpresponse();
            Error error = new Error();
            error.name = 'token error';
            String errorResponse = Json.serialize(error);
            httpresponse.setBody(errorResponse);
        }
        system.debug('PayPalServicer.getEndpointResponse()'
            + ': pageReference = ' + pageReference
            + ', requestBody = ' + requestBody
        );

        Httpresponse httpresponse = new Integration().getEndpointResponse(pageReference, pageReferenceParts[0], new Map<String, String> {
            'Content-Type'   => 'application/json',
            'Content-Length' => '' + requestBody.length(),
            'Accept'         => 'application/json',
            'User-Agent'     => 'PayPalSDK/NIW/HATEOAS',
            'authorization'  => authorizationToken,
            'PayPal-Partner-Attribution-Id' => 'roundCorner_SP'
        }, requestBody, pageReference.getParameters().get('Id'));

        if (test.isRunningTest() == true) {
            httpresponse = getPaymentResponse();
        }

        return httpresponse;
    }

    public String getRequest_PROCESS_CC_WITHOUT_TOKEN(PageReference pageReference) {
        ProcessCCWithoutTokenRequest processCCWithoutTokenRequest = new ProcessCCWithoutTokenRequest();
        processCCWithoutTokenRequest.intent = 'sale';

        Payer_Without_Token payer = new Payer_Without_Token();
        payer.payer_info = new payer_info();
        payer.payer_info.email = blank(pageReference.getParameters().get(CONTACT_EMAIL));
        payer.payment_method = 'credit_card';
        payer.funding_instruments = new Funding_Instrument_Without_Token[] {};
        Funding_Instrument_Without_Token funding_instrument = new Funding_Instrument_Without_Token();
        funding_instrument.credit_card = new Credit_Card();
        funding_instrument.credit_card.card_number = blank(pageReference.getParameters().get(CREDIT_CARD_NUMBER));
        funding_instrument.credit_card.type = blank(pageReference.getParameters().get(CREDIT_CARD_MOP));
        funding_instrument.credit_card.expire_month = blank(pageReference.getParameters().get(CREDITCARD_EX_MONTH));
        funding_instrument.credit_card.expire_year = blank(pageReference.getParameters().get(CREDITCARD_EX_YEAR));
        funding_instrument.credit_card.cvv2 = blank(pageReference.getParameters().get(CREDIT_CARD_CVV2));
        funding_instrument.credit_card.first_name = blank(pageReference.getParameters().get(FIRSTNAME));
        funding_instrument.credit_card.last_name = blank(pageReference.getParameters().get(LASTNAME));
        funding_instrument.credit_card.billing_address = new Billing_Address();
        funding_instrument.credit_card.billing_address.line1 = blank(pageReference.getParameters().get(ADDRESS));
        funding_instrument.credit_card.billing_address.city = blank(pageReference.getParameters().get(ADDR_CITY));
        funding_instrument.credit_card.billing_address.postal_code = blank(pageReference.getParameters().get(ZIPCODE));
        funding_instrument.credit_card.billing_address.state = blank(pageReference.getParameters().get(ADDR_STATE));
        funding_instrument.credit_card.billing_address.country_code = blank(pageReference.getParameters().get(ADDR_COUNTRY_CODE));
        payer.funding_instruments.add(funding_instrument);
        processCCWithoutTokenRequest.payer = payer;
        
        
        

        processCCWithoutTokenRequest.transactions = new Transactions[] {};
        Transactions transactions = new Transactions();
        transactions.amount = new Amount();
        transactions.amount.entered_currency = blank(pageReference.getParameters().get(CURRENCY_CODE));
        transactions.amount.total = blank(pageReference.getParameters().get(TOTAL_AMOUNT));
        transactions.description = blank(pageReference.getParameters().get(OPPORTUNITY_DESCRIPTION));
        transactions.invoice_number = blank(pageReference.getParameters().get(INVOICE_ID));
        transactions.custom = blank(pageReference.getParameters().get(CUSTOM_VAR));
        processCCWithoutTokenRequest.transactions.add(transactions);

        String requestBody = Json.serialize(processCCWithoutTokenRequest);
        requestBody = requestBody.replace('card_number', 'number');
        requestBody = requestBody.replace('entered_currency', 'currency');
        return requestBody;
    }

    public Map<String,String> processEndpointResponse(Attachment responseData) {
        String responseJson = responseData.Body.toString();
        Map<String,String> responseMap = new Map<String,String>();
        if (responseJson == null || responseJson == '') {
            responseMap.put(RESPONSEMESSAGE, 'Unable to process payment, please try again.');
            return responseMap;
        }
        system.debug('responseJson-->'+responseJson);
        responseJson = responseJson.replace('currency', 'entered_currency');
        
        
        ProcessCCWithoutTokenResponse processCCWithoutTokenResponse = (PaymentServicer_PaypalTransaction.ProcessCCWithoutTokenResponse)Json.deserialize(responseJson, PaymentServicer_PaypalTransaction.ProcessCCWithoutTokenResponse.class);
        Error error = (PaymentServicer_PaypalTransaction.Error)Json.deserialize(responseJson, PaymentServicer_PaypalTransaction.Error.class);

        
        try {
            if (processCCWithoutTokenResponse.transactions != null && processCCWithoutTokenResponse.transactions.size() > 0 && processCCWithoutTokenResponse.transactions[0].related_resources != null && processCCWithoutTokenResponse.transactions[0].related_resources.size() > 0) {
                responseMap.put(TRANSACTIONID, processCCWithoutTokenResponse.transactions[0].related_resources[0].sale.id);
            }
            responseMap.put(DATE_TIME,+ string.valueOf(DateTime.now()));

            string message = processCCWithoutTokenResponse.state;

            message = message == null? '' : message;

            if (message == '' && error.name != null) {
                //message = error.name;
                //message += '\n' + error.message;
                if ('token error'.equalsIgnoreCase(error.name)) {
                    message = 'Unable to process payment, please try again.';
                } else {
                    message = 'Please check card details: ' + error.name;
                }
            }
            responseMap.put(RESPONSEMESSAGE, message);
            Boolean is_Success = message.containsIgnoreCase('approved');
            responseMap.put(ISSUCCESS,''+is_Success);

        } catch (Exception pException) {
            throw new Integration.InvalidPayloadFormatException(pException);
        }

        return responseMap;

    }

    public map<string,string> processPayment(map<string,string> paymentData, Id campaignId) {
        try {
            Campaign[] campaignList = [
                SELECT Paypal_Merchant__r.Merchant_Password__c
                     , Paypal_Merchant__r.Merchant_User_Id__c
                     , Paypal_Merchant__r.Production_Endpoint__c
                  FROM Campaign
                 WHERE Id = :campaignId
                   AND Paypal_Merchant__c <> NULL
                 LIMIT 1
            ];

            if (campaignList.isEmpty()) {
                return new Map<String, String> {
                    PaymentServicer_PaypalTransaction.ISSUCCESS => 'false',
                    PaymentServicer_PaypalTransaction.RESPONSEMESSAGE => 'No campaign and/or paypal merchant found'
                };
            }

            Paypal_Merchant__c paypalMerchant = campaignList[0].Paypal_Merchant__r;

            // Merchant name
            paymentData.put(PaymentServicer_PaypalTransaction.MERCHANT_NAME, 'RC');

            // User/pass
            paymentData.put(PaymentServicer_PaypalTransaction.MERCHANT_USER_ID, paypalMerchant.Merchant_User_Id__c);
            paymentData.put(PaymentServicer_PaypalTransaction.MERCHANT_PASS, paypalMerchant.Merchant_Password__c);

            // Production or sandbox?
            if (paypalMerchant.Production_Endpoint__c == true) {
                paymentData.put(PaymentServicer_PaypalTransaction.ENDPOINT, 'Production');
            } else {
                paymentData.put(PaymentServicer_PaypalTransaction.ENDPOINT, 'Sandbox');
            }
        } catch (System.Exception problem) {
            return new Map<String, String> {
                PaymentServicer_PaypalTransaction.ISSUCCESS => 'false',
                PaymentServicer_PaypalTransaction.RESPONSEMESSAGE => problem.getTypeName() + ': ' + problem.getMessage()
            };
        }

        return processPayment(paymentData);
    }

    public map<string,string> processPayment(map<string,string> paymentData) {
        return processEndpointResponse(
            new Integration().getEndpointResponseAsAttachment(
                getEndpointResponse(
                    getEndpoint(paymentData)
                )
            )
        );
    }

    public virtual String encodeValue(String value) {
        return value == null ? '' : EncodingUtil.urlEncode(value.trim(), 'UTF-8');
    }

    public virtual String encodeValue(Object value) {
        return value == null ? encodeValue('') : encodeValue(String.valueof(value));
    }

    public String blank(String value) {
         return value == null ? '' : value;
    }

    public string getCreditCardType(String accountNumber) {
        String result = 'unknown';

        if (Pattern.compile('^5[1-5][0-9]{14}$').matcher(accountNumber).matches()) {
            result = 'mastercard';
        } else if (Pattern.compile('^4[0-9]{12}(?:[0-9]{3})?$').matcher(accountNumber).matches()) {
            result = 'visa';
        } else if (Pattern.compile('^3[47][0-9]{13}$').matcher(accountNumber).matches()) {
            result = 'amex';
        } else if (Pattern.compile('^6(?:011|5[0-9]{2})[0-9]{12}$').matcher(accountNumber).matches()) {
            result = 'discover';
        } else if (Pattern.compile('^3(?:0[0-5]|[68][0-9])[0-9]{11}$').matcher(accountNumber).matches()) {
            result = 'diners club';
        } else if (Pattern.compile('^(?:2131|1800|35/d{3})/d{11}$').matcher(accountNumber).matches()) {
            result = 'jcb';
        }

        return result;
    }

    public HttpResponse getPaymentResponse () {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setBody('{"id":"PAY-1R7724053J9229734KPGP6PQ","create_time":"2014-07-21T11:53:34Z","update_time":"2014-07-21T11:53:35Z","state":"approved","intent":"sale","payer":{"payment_method":"credit_card","funding_instruments":[{"credit_card":{"type":"visa","number":"xxxxxxxxxxxx9061","expire_month":"6","expire_year":"2020","first_name":"Amol","last_name":"Sable","billing_address":{"line1":"Ab+Davis","city":"Lott","state":"Texas","postal_code":"10007","country_code":"US"}}}]},"transactions":[{"amount":{"total":"15.00","currency":"USD","details":{"subtotal":"15.00"}},"related_resources":[{"sale":{"id":"6VV53414YP643405H","create_time":"2014-07-21T11:53:34Z","update_time":"2014-07-21T11:53:35Z","amount":{"total":"15.00","currency":"USD"},"state":"completed","parent_payment":"PAY-1R7724053J9229734KPGP6PQ","links":[{"href":"https://api.sandbox.paypal.com/v1/payments/sale/6VV53414YP643405H","rel":"self","method":"GET"},{"href":"https://api.sandbox.paypal.com/v1/payments/sale/6VV53414YP643405H/refund","rel":"refund","method":"POST"},{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAY-1R7724053J9229734KPGP6PQ","rel":"parent_payment","method":"GET"}]}}]}],"links":[{"href":"https://api.sandbox.paypal.com/v1/payments/payment/PAY-1R7724053J9229734KPGP6PQ","rel":"self","method":"GET"}]}');
        return httpResponse;
    }
}