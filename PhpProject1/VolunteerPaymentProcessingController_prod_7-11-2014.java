public with sharing class VolunteerPaymentProcessingController extends SobjectExtension {
    public boolean booleanJoinGirlScout {get;set;}
    public boolean booleanBuysGirlScout {get;set;}
    public boolean booleanSupportTenGirls {get;set;}
    public boolean booleanOther {get;set;}
    public double NotAtThisTime {get;set;}

    public double otherPayment {get;set;}

    public String councilId;
    public String firstName {get;set;}
    public String lastName {get;set;}
    public String address {get;set;}
    public String city {get;set;}
    public String country {get;set;}
    public String state {get;set;}
    public String zipCode {get;set;}
    public String cardHolderName {get;set;}
    public String cardNumber {get;set;}
    public String expMonth {get;set;}
    public String expYear {get;set;}
    public String contactId {get; set;}
    public String opportunityId {get; set;}

    public String optionsRadios1 {get;set;}
    public String optionsRadios2 {get;set;}
    public String optionsRadios3 {get;set;}

    public decimal option1Value{get;set;}
    public decimal option2Value{get;set;}
    public decimal option3Value{get;set;}
    public decimal option4Value{get;set;}
    public decimal option5Value{get;set;}
    public decimal option6Value{get;set;}
    
    public decimal optionValue1{get;set;}
    public decimal optionValue2{get;set;}
    public decimal optionValue3{get;set;}
    public decimal optionValue4{get;set;}
    public decimal optionValue5{get;set;}
    public decimal optionValue6{get;set;}
    public decimal optionValue7{get;set;}
    
    public double amountValue {get;set;}
    public decimal total {get;set;}
    public Map<String, decimal> donationMap {get;set;}

    public String securityCode {get;set;}
    public String email {get;set;}
    public decimal donationPriseAmount;
    public String campaignMemberIds;

    public String primaryContactFullName;
    public String primaryContactEmail;
    
    public String amount {get;set;}
    public boolean acceptGSPromiseAndLaw {get;set;}
    public List<Opportunity> opportunityTransactionList {get; set;}

    private Opportunity membershipOpportunity;
    private Opportunity OldDonationopportunity;
    private map<Id, PricebookEntry> priceBookEntryMap;
    private PricebookEntry donationPricebookEntry;
    private Account councilAccount;
    private Contact contact;
    private static Integer counterUnableToLockRow = 0;

    public Boolean confirmTransactions { set; get; }

    private static final string GIRL_SCOUTS_USA_PRICEBOOK = 'Girl Scouts USA';
    private static final string DONATION = 'Donation';

    private set<Id> opportunityIdSet = new set<Id>();
    private Map<String, GSA_payment__c> paymentOptionsMap;

    private boolean backFlag;
    private Boolean noDelete;

    public VolunteerPaymentProcessingController() {
        acceptGSPromiseAndLaw = false;
        NotAtThisTime = 0;
        total = 0;
        donationPriseAmount = 0;
        confirmTransactions = false;
        counterUnableToLockRow = 0;

        noDelete = false;

        donationMap = new Map<String, decimal>();
        OldDonationopportunity  =  new Opportunity();
        membershipOpportunity = new Opportunity();
        donationPricebookEntry = new PricebookEntry();
        priceBookEntryMap = new map<Id, PricebookEntry>();

        fillPricebookEntryList();

        for(PricebookEntry varPricebookEntry : priceBookEntryMap.values()) {
            if(varPricebookEntry.Product2.Name.equalsIgnoreCase(DONATION) && varPricebookEntry.Pricebook2.Name.equalsIgnoreCase(GIRL_SCOUTS_USA_PRICEBOOK)) {
                donationPricebookEntry = priceBookEntryMap.get(varPricebookEntry.Id);
                break;
            }
        }

        if(Apexpages.currentPage().getParameters().containsKey('CouncilId')) {
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');
            councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);
            VolunteerController.councilAccount = councilAccount;
        }

        if (Apexpages.currentPage().getParameters().containsKey('ContactId'))
            contactId = Apexpages.currentPage().getParameters().get('ContactId');

        if (Apexpages.currentPage().getParameters().containsKey('OpportunityId'))
            opportunityId = Apexpages.currentPage().getParameters().get('OpportunityId');

        if (Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds'))
            campaignMemberIds = Apexpages.currentPage().getParameters().get('CampaignMemberIds');

        if(contactId != null) {
            List<Contact> contactList = [
                Select Id
                     , Name
                     , Email
                     , LastName
                     , AccountId
                     , VolunteerPage1URL__c
                     , IsVoluntter1stPageDone__c
                  from Contact
                 Where Id = :contactId
            ];
            contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();
            primaryContactFullName = contact.Name;
            primaryContactEmail = contact.Email;
        }

        if(opportunityId != null && opportunityId != '')
            membershipOpportunity = [
                select Id
                     , Name
                     , OwnerId
                     , rC_Giving__Parent__c
                     , RecordTypeId
                  from Opportunity
                 where Id = : opportunityId
            ];

        paymentOptionsMap = GSA_payment__c.getAll();
        system.debug('--------paymentOptionsMap---------'+paymentOptionsMap);
        
        GSA_payment__c financialAssitanceDonation1_Setting = GSA_payment__c.getValues('FinancialAssitanceDonation1');
        option1Value = paymentOptionsMap.get('Donation1').amountDonate__c.setScale(2);
        option2Value = paymentOptionsMap.get('Donation2').amountDonate__c.setScale(2);
        option3Value = paymentOptionsMap.get('Donation3').amountDonate__c.setScale(2);
        option4Value = paymentOptionsMap.get('Donation4').amountDonate__c.setScale(2);
        option5Value = paymentOptionsMap.get('Donation5').amountDonate__c.setScale(2);
        option6Value = paymentOptionsMap.get('Donation6').amountDonate__c.setScale(2);

        if(opportunityId != null && opportunityId != ''){
            OpportunityLineItem[] opportunityLineItemList = [
                Select PricebookEntry.UseStandardPrice
                     , PricebookEntry.UnitPrice
                     , PricebookEntry.Product2Id
                     , PricebookEntry.Name
                     , PricebookEntryId
                     , OpportunityId
                     , ListPrice
                  From OpportunityLineItem
                 where OpportunityId = :opportunityId
            ];

            OpportunityLineItem opportunityLineItem = opportunityLineItemList.size() > 0 ? opportunityLineItemList[0] : null;

            if(opportunityLineItem != null && opportunityLineItem.PricebookEntry.Name != null) {
                donationMap.put(opportunityLineItem.PricebookEntry.Name, opportunityLineItem.PricebookEntry.UnitPrice.setScale(2));

                total = opportunityLineItem.PricebookEntry.UnitPrice.setScale(2);
                donationPriseAmount = total;
            }
        }
    }

    public PageReference createTransactionRecord() {
        Savepoint savepoint = Database.setSavepoint();
        try{
            noDelete = true;
            opportunityTransactionList = new List<Opportunity>();
            
            if(amountValue == option1Value)
                optionValue1 = amountValue;
            else if(amountValue == option2Value)
                optionValue2 = amountValue;
            else if(amountValue == option3Value)
                optionValue3 = amountValue;
            else if(amountValue == option4Value)
                optionValue4 = amountValue;
            else if(amountValue == option5Value)
                optionValue5 = amountValue;
            else if(amountValue == option6Value)
                optionValue6 = amountValue;
            else if(amountValue != null && amountValue != 0)
                optionValue7 = amountValue;

            total = donationPriseAmount;
            total = total + amountValue;
            
            //if(amountValue != null && amountValue.trim() != '' && contactId != null && contactId != '')
            if(amountValue != null && amountValue > 0 && contactId != null && contactId != '') {

                if(amountValue == 0) {
                   donationMap.remove('Financial Assistance Donation');
                   return null;
                }

                if(contact != null && contact.Id != null && councilAccount != null && councilAccount.Id != null) {
                    Opportunity donationOpp = new Opportunity(
                          Name = 'Financial Assistance Donation'
                        , AccountId = contact.AccountId
                        , CampaignId = councilAccount.Payment_Campaign__c
                        , rC_Giving__Activity_Type__c = 'Donation'
                        , rC_Giving__Reporting_Schedule__c = 'One Payment'
                        , rC_Giving__Giving_Amount__c = amountValue
                        , StageName = 'Open'
                        , CloseDate= system.Today()
                        , RecordTypeId = VolunteerRegistrationUtilty.getOpportunityRecordTypeId(VolunteerRegistrationUtilty.OPPORTUNITY_DONATION_RECORDTYPE)
                        , PriceBook2Id = donationPricebookEntry.Pricebook2Id
                    );

                    if(membershipOpportunity != null && membershipOpportunity.OwnerId != null)
                        donationOpp.OwnerId = membershipOpportunity.OwnerId;

                    opportunityTransactionList.add(donationOpp);
                }

                for(Opportunity opportunity : opportunityTransactionList){
                    if(opportunity != null && opportunity.Name != null)
                        donationMap.put(opportunity.Name, decimal.ValueOf(amountValue).setScale(2));

                 system.debug('donationMap##############33'+donationMap);
                }
            }
        } catch(System.exception pException) {
        system.debug('##############pException: '+pException);
            return addErrorMessageAndRollback(savepoint, pException);
        }
            return null;
    }
    public boolean pastDate() {
        boolean pastBoolean = true;
        if(Integer.valueOf(expYear) < system.today().year()) {
            pastBoolean = false;
        }else if(Integer.valueOf(expYear) == system.today().year()){
            if(Integer.valueOf(expMonth) < system.today().month()){
                 pastBoolean = false;
            }else{
                pastBoolean = true;
            }
        }else{
             pastBoolean = true;
        }
        return pastBoolean;
    }
    public Pagereference processMyOrder() {
        counterUnableToLockRow++;
        Savepoint savepoint = Database.setSavepoint();
        try {
            boolean pastDateBoolean = pastDate();
            if(pastDateBoolean == false)
                return addErrorMessage('Card Expiration Date Cannot be in the past.');

            if(backFlag == true && OldDonationopportunity.Id != null && noDelete == true) {            
                    new WithoutSharing().deleteData(new Sobject[]{OldDonationopportunity});
                    OldDonationopportunity.clear();
                    OldDonationopportunity  =  new Opportunity();
            }
            if(opportunityTransactionList != null && opportunityTransactionList.size() > 0 && opportunityTransactionList[0].id == null) {

                    List<Database.Saveresult> oppSaveResult = Database.insert(opportunityTransactionList);
                    if(!oppSaveResult.isEmpty() && oppSaveResult.size() > 0) {
                        OldDonationopportunity = opportunityTransactionList[0];

                        //Insert opp contact role
                        OpportunityContactRole opportunityContactRole = new OpportunityContactRole (
                            OpportunityId = oppSaveResult[0].getId(),
                            ContactId = contactId,
                            Role = 'Other',
                            IsPrimary = true
                        );
                        insert opportunityContactRole;

                        //Insert opp line item
                        OpportunityLineItem donationLineItem = new OpportunityLineItem();
                        donationLineItem.PricebookEntryId = donationPricebookEntry.Id;
                        donationLineItem.OpportunityId = oppSaveResult[0].getId();
                        donationLineItem.Quantity = 1;
                        donationLineItem.UnitPrice = amountValue;                    
                        insert donationLineItem;

                        /****** Update owner of Transactions ******/
                        for(Database.Saveresult saveResult : oppSaveResult) {
                            opportunityIdSet.add(saveResult.getId());
                        }
                        if(opportunityIdSet != null && opportunityIdSet.size() > 0) {
                            List<Opportunity> OpportunityList = [
                                Select RecordType.Name
                                     , RecordTypeId
                                     , rC_Giving__Parent__c
                                     , rC_Giving__Parent__r.Id
                                     , rC_Giving__Parent__r.OwnerId
                                  From Opportunity
                                 where RecordType.Name = 'Transaction'
                                   and rC_Giving__Parent__c IN : opportunityIdSet
                            ];
    
                            if(OpportunityList.size() > 0) {
                                for(Opportunity opp : OpportunityList) {
                                    opp.OwnerId = opp.rC_Giving__Parent__r.OwnerId;
                                }
                                new WithoutSharing().updateData(OpportunityList);
                            }
                        }
                    }
            }
        }catch(System.exception pException) {
            if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                if(counterUnableToLockRow <= 5) {
                    Database.rollback(savepoint);
                    processMyOrder();
                }
                else
                    return addErrorMessage('Record is locked by another user. Please re-submit the page once more.');
            }
            else
                return addErrorMessageAndRollback(savepoint, pException);
        }
        counterUnableToLockRow = 0;
        confirmTransactions = true;
        return null;
    }

    public PageReference processPaypalTransactionsUndo() {
        confirmTransactions = false;
        backFlag = true;
        noDelete = false;
        return null;
    }

    public PageReference processPaypalTransactions() {
        List<Opportunity> opportunityTransactionChargeableList = null;
        backFlag = false;
        try {
            Set<Id> opportunityParentIds = new set<Id>();
            if (opportunityTransactionList != null && opportunityTransactionList.size() != 0) {
                opportunityParentIds.addAll(new Map<Id, Opportunity>(opportunityTransactionList).keySet());
            }

            // donation opps
            opportunityParentIds = opportunityParentIds.clone();
            opportunityParentIds.add(membershipOpportunity.Id);
            opportunityParentIds.remove(null);
            system.debug('***opportunityParentIds****'+opportunityParentIds);
            opportunityTransactionChargeableList = [
                SELECT Amount
                     , StageName
                     , rC_Giving__Parent__r.CampaignId
                  FROM Opportunity
                 WHERE RecordType.Name = 'Transaction'
                   AND rC_Giving__Parent__c IN :opportunityParentIds
                   FOR UPDATE
            ];
            boolean sendReciept = false;
              List<PaypalResponseLog__c> lstpaypallog=new List<PaypalResponseLog__c>();
            for(Opportunity opportunityTransaction : opportunityTransactionChargeableList) {
                system.debug('opportunityTransaction-->'+opportunityTransaction);
                Boolean isStageOpen = 'Open'.equalsIgnoreCase(opportunityTransaction.StageName);
                Boolean isStagePendingFailed = 'Pending Failed'.equalsIgnoreCase(opportunityTransaction.StageName);
                Boolean isStageBlank = null == opportunityTransaction.StageName;

                if (isStageOpen == false && isStagePendingFailed == false && isStageBlank == false) {
                    continue;
                }

                if (opportunityTransaction.rC_Giving__Parent__r.CampaignId == null) {
                    continue;
                }

                if (opportunityTransaction.Amount == null || opportunityTransaction.Amount == 0) {
                    continue;
                }
                // Run it
                Map<String, String> transactionResult = new PaymentServicer_PaypalTransaction().processPayment(new Map<String, String> {
                    PaymentServicer_PaypalTransaction.CREDITCARD_EX_MONTH => expMonth,
                    PaymentServicer_PaypalTransaction.CREDITCARD_EX_YEAR => expYear,
                    PaymentServicer_PaypalTransaction.CREDIT_CARD_NUMBER => cardNumber,
                    PaymentServicer_PaypalTransaction.CREDIT_CARD_CVV2 => securityCode,
                    PaymentServicer_PaypalTransaction.FULLNAME => cardHolderName,
                    PaymentServicer_PaypalTransaction.CUSTOM_VAR => primaryContactFullName,
                    PaymentServicer_PaypalTransaction.ADDRESS => address,
                    PaymentServicer_PaypalTransaction.ADDR_CITY => city,
                    PaymentServicer_PaypalTransaction.ADDR_STATE => state,
                    PaymentServicer_PaypalTransaction.ADDR_COUNTRY_CODE => 'US',
                    PaymentServicer_PaypalTransaction.ZIPCODE => zipCode,
                    PaymentServicer_PaypalTransaction.TOTAL_AMOUNT => '' + opportunityTransaction.Amount,
                    PaymentServicer_PaypalTransaction.INVOICE_ID => '' + opportunityTransaction.id,
                    PaymentServicer_PaypalTransaction.CONTACT_EMAIL => primaryContactEmail
                }, opportunityTransaction.rC_Giving__Parent__r.CampaignId);

                // Success/failure?
                if (transactionResult.get(PaymentServicer_PaypalTransaction.ISSUCCESS) == 'true') {
                    opportunityTransaction.StageName = 'Completed';
                    sendReciept = true;
                } else {
                    opportunityTransaction.StageName = 'Pending Failed';
                    noDelete = false;
                    // Add error messages
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, 'Charge failure: ' + transactionResult.get(PaymentServicer_PaypalTransaction.RESPONSEMESSAGE)));
                }

                // Always set
                opportunityTransaction.rC_Connect__Response_Code__c = transactionResult.get(PaymentServicer_PaypalTransaction.TRANSACTIONID);
                opportunityTransaction.rC_Connect__Response_Date_Time__c = DateTime.now();
                opportunityTransaction.rC_Connect__Response_Message__c = transactionResult.get(PaymentServicer_PaypalTransaction.RESPONSEMESSAGE);
          
            
                System.debug('opportunityTransaction.Id==>'+opportunityTransaction.Id);
                /****************** Track Paypal Reponse Messages Log*******************/
                PaypalResponseLog__c paypallog=new PaypalResponseLog__c();
                paypallog.Response_Code__c=transactionResult.get(PaymentServicer_PaypalTransaction.TRANSACTIONID);
                paypallog.Response_Date_Time__c=DateTime.now();
                paypallog.Response_Message__c= transactionResult.get(PaymentServicer_PaypalTransaction.RESPONSEMESSAGE);
                paypallog.Transaction_Opportunity__c=opportunityTransaction.Id;
                paypallog.Name='Volunteer registration Paypal Response';
                System.debug('Try to Insert data into PaypalResponseLog__c ======' );
                lstpaypallog.add(paypallog);
                //insert paypallog;
                System.debug('After inser data into PaypalResponseLog__c ==>'+paypallog);
                /****************** Track Paypal Reponse Messages Log*******************/
            
            
            }

                System.debug('lstpaypallog.size() ==>'+lstpaypallog.size());
            if(lstpaypallog!=null && lstpaypallog.size()>0)
            {
           // insert lstpaypallog;
                Database.SaveResult[] srList = Database.insert(lstpaypallog, false);

                    // Iterate through each returned result
                    for (Database.SaveResult sr : srList) {
                        if (sr.isSuccess()) {
                            // Operation was successful, so get the ID of the record that was processed
                            System.debug('Successfully inserted paypal log ID: ' + sr.getId());
                        }
                        else {
                            // Operation failed, so get all errors                
                            for(Database.Error err : sr.getErrors()) {
                                System.debug('The following error has occurred.');                    
                                System.debug(err.getStatusCode() + ': ' + err.getMessage());
                                System.debug('paypal log fields that affected this error: ' + err.getFields());
                            }
                        }
                    }
            
            
            }
            // Done
  /********************************* Track Paypal Reponse Messages Log*******************/         
            /*
            List<Contact> contactList = [
            Select Id
                , Name
                 , VolunteerPage1URL__c
                 , IsVoluntter1stPageDone__c
              from Contact
             Where Id = :contactId
            ];
            Contact contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();
            */
            if(sendReciept) {
                SendReceipt SR = new SendReceipt();
                SR.sendEmail(donationMap,contact.Id,'noGirl',total);
            }
        } catch(System.Exception problem) {
            return addErrorMessage(problem);
        }

        // Update the transactions
        Savepoint savepoint = Database.setSavepoint();

        try {
            updateOpportunityTransactionChargeableList(opportunityTransactionChargeableList, 0);
        } catch(Exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }

        // Errors to date?
        if (ApexPages.hasMessages(ApexPages.Severity.Error)) {
            confirmTransactions = false;
            noDelete = false;
            return null;
        }

        /*/ Done
        List<Contact> contactList = [
            Select Id
                , Name
                 , VolunteerPage1URL__c
                 , IsVoluntter1stPageDone__c
              from Contact
             Where Id = :contactId
        ];
        Contact contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();
        */

        if(contact != null && contact.Id != null)
            VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_DemographicsInformation' + '?ContactId='+contactId + '&CouncilId='+CouncilId+'&CampaignMemberIds='+campaignMemberIds+'&OpportunityId='+opportunityId, contact);

        PageReference demographicsInfoPage = Page.Volunteer_DemographicsInformation;
        if(contactId != null)
            demographicsInfoPage.getParameters().put('ContactId', contactId);
        if(campaignMemberIds != null)
            demographicsInfoPage.getParameters().put('CampaignMemberIds',campaignMemberIds);
        if(councilId != null)
            demographicsInfoPage.getParameters().put('CouncilId',councilId);
        if(opportunityId != null)
            demographicsInfoPage.getParameters().put('OpportunityId',opportunityId);

        demographicsInfoPage.setRedirect(true);
        return demographicsInfoPage;

    }

    public void updateOpportunityTransactionChargeableList(List<Opportunity> opportunityTransactionChargeableList, Integer failureCount) {
        try {
            if (opportunityTransactionChargeableList.isEmpty() == false)
                //update opportunityTransactionChargeableList;
                new WithoutSharing().updateData(opportunityTransactionChargeableList);
                system.debug('***opportunityTransactionChargeableList****'+opportunityTransactionChargeableList);
        } catch(System.Exception problem) {
            if(failureCount < 5)
                updateOpportunityTransactionChargeableList(opportunityTransactionChargeableList, failureCount++);
            else
                throw problem;
        }
    }

    public List<SelectOption> getlistexpYear() {
        List<SelectOption> expOptions = new List<SelectOption>();
        expOptions.add(new SelectOption('--None--', '--None--'));
        for(Integer i = system.today().year(); i < (system.today().year()+12); i++) {
            expOptions.add(new SelectOption(i+'',i+' '));
        }
        return expOptions;
    }

    public List<SelectOption> getlistCountryItems() {
        List<SelectOption> countryOptions = new List<SelectOption>();
        map<String, CountryNames__c> countryNamesMap = CountryNames__c.getAll();

        //countryOptions.add(new Selectoption('--None--', '--None--'));
        if(!countryNamesMap.isEmpty()){
            for(String countryName : countryNamesMap.keySet())
                countryOptions.add(new Selectoption(countryName, countryName));
        }
        return countryOptions;
    }

    public List<SelectOption> getlistStateItems() {

        List<StateNames__c> stateNamesList = StateNames__c.getAll().values();
        List<String> stateNamesSortedList = new List<String>();
        List<SelectOption> stateOptions = new List<SelectOption>();

        for(StateNames__c stateName : stateNamesList){
            if(stateName != null)
                stateNamesSortedList.add(stateName.Name);
        }

        if(stateNamesSortedList != null && stateNamesSortedList.size() > 0)
            stateNamesSortedList.sort();

        stateOptions.add(new SelectOption('--None--', '--None--'));

        for(String stateName : stateNamesSortedList){
            if(stateName != null)
                stateOptions.add(new SelectOption(stateName, stateName));
        }
        return stateOptions;
    }

    public PageReference fillPaymentMailingAddress(){
        Savepoint savepoint = Database.setSavepoint();
        try{
            List<Contact> contactList = [Select Name, MailingStreet, MailingState, MailingPostalCode, MailingLongitude, MailingLatitude, MailingCountry, MailingCity, Id From Contact where Id= :contactId];
            Contact contact = (contactList != null && contactList.size() > 0 ) ? contactList[0] : new Contact();

            if(contact.Id != null){
                address = (contact.MailingStreet != null) ? contact.MailingStreet : '';
                city = (contact.MailingCity != null) ? contact.MailingCity : '';
                country = (contact.MailingCountry != null) ? contact.MailingCountry.toUppercase() : '';

                if(contact.MailingState != null){
                    //state = contact.MailingState;
                    //state = state.substring(0,1).toUpperCase() + state.substring(1,state.length()).toLowerCase();
                    //state = contact.MailingState;
                    String stateName = VolunteerRegistrationUtilty.getStateName(contact.MailingState);
                    state = (stateName != null && stateName != '') ? stateName : contact.MailingState;
                    system.debug('###################################'+state);
                }

                zipCode = (contact.MailingPostalCode != null) ? contact.MailingPostalCode : '';
            }
        }catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public List<SelectOption> getlistexpMonth() {
        List<SelectOption> expOptions = new List<SelectOption>();
        expOptions.add(new SelectOption('--None--', '--None--'));
        expOptions.add(new SelectOption('01', '01'));
        expOptions.add(new SelectOption('02', '02'));
        expOptions.add(new SelectOption('03', '03'));
        expOptions.add(new SelectOption('04', '04'));
        expOptions.add(new SelectOption('05', '05'));
        expOptions.add(new SelectOption('06', '06'));
        expOptions.add(new SelectOption('07', '07'));
        expOptions.add(new SelectOption('08', '08'));
        expOptions.add(new SelectOption('09', '09'));
        expOptions.add(new SelectOption('10', '10'));
        expOptions.add(new SelectOption('11', '11'));
        expOptions.add(new SelectOption('12', '12'));
        return expOptions;
    }

    public void fillPricebookEntryList() {
        List<PricebookEntry> PricebookEntryList = [
            Select Id
                 , Name
                 , Pricebook2.Description
                 , Pricebook2.IsActive
                 , Pricebook2.Name
                 , Pricebook2.Id
                 , Pricebook2Id
                 , Product2Id
                 , UnitPrice
                 , Product2.rC_Giving__Start_Date__c
                 , Product2.rC_Giving__End_Date__c
                 , Product2.Name
              From PricebookEntry
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
        ];
        system.debug('PricebookEntryList=====:  ' + PricebookEntryList);

        if(PricebookEntryList != null && PricebookEntryList.size() > 0) {
            for(PricebookEntry pricebookEntry : PricebookEntryList)
                priceBookEntryMap.put(pricebookEntry.Id, pricebookEntry);
        }
    }
    public OpportunityLineItem createOpportunityLineItem(PricebookEntry priceBookEntry, Opportunity newOpportunity) {
        OpportunityLineItem opportunityLineItem = new OpportunityLineItem();
        opportunityLineItem.PricebookEntryId = priceBookEntry.Id;
        opportunityLineItem.OpportunityId = newOpportunity.Id;
        opportunityLineItem.Quantity = 1;
        opportunityLineItem.UnitPrice = newOpportunity.rC_Giving__Giving_Amount__c;
        return opportunityLineItem;
    }

    //new WithoutSharing().updateData();

    public without sharing class WithoutSharing {
        public void insertData(Sobject[] sobjectRefList) {
            insert sobjectRefList;
        }

        public void upsertData(Sobject[] sobjectRefList) {
            upsert sobjectRefList;
        }

        public void updateData(Sobject[] sobjectRefList) {
            update sobjectRefList;
        }
        
        public void deleteData(Sobject[] sobjectRefList) {
            delete sobjectRefList;
        }
    }


}