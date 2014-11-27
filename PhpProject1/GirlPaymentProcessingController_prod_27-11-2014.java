public with sharing class GirlPaymentProcessingController extends SobjectExtension {

    public boolean  booleanJoinGirlScout     { get; set; }
    public boolean  booleanBuysGirlScout     { get; set; }
    public boolean  booleanSupportTenGirls   { get; set; }
    public boolean  booleanOther             { get ;set; }
    public double   NotAtThisTime            { get; set; }
    public double   otherPayment             { get; set; }

    public String firstName         { get; set; }
    public String lastName          { get; set; }
    public String address           { get; set; }
    public String city              { get; set; }
    public String country           { get; set; }
    public String state             { get; set; }
    public String zipCode           { get; set; }
    public String cardHolderName    { get; set; }
    public String cardNumber        { get; set; }
    public String expMonth          { get; set; }
    public String expYear           { get; set; }
    public String contactId         { get; set; }
    public String opportunityId     { get; set; }
    public String parentContactId   { get; set; }

    public String optionsRadios1 { get; set; }
    public String optionsRadios2 { get; set; }
    public String optionsRadios3 { get; set; }

    public decimal option1Value { get; set; }
    public decimal option2Value { get; set; }
    public decimal option3Value { get; set; }
    public decimal option4Value { get; set; }
    public decimal option5Value { get; set; }
    public decimal option6Value { get; set; }

    public decimal optionValue1 { get; set; }
    public decimal optionValue2 { get; set; }
    public decimal optionValue3 { get; set; }
    public decimal optionValue4 { get; set; }
    public decimal optionValue5 { get; set; }
    public decimal optionValue6 { get; set; }
    public decimal optionValue7 { get; set; }

    public decimal total        { get; set; }
    public double amountValue   { get; set; }

    public String securityCode  { get; set; }
    public String email         { get; set; }
    public String amount        { get; set; }

    public Boolean confirmTransactions      { set; get; }
    public Boolean acceptGSPromiseAndLaw    { get; set; }

    public Map <String, decimal> donationMap                 { get; set; }
    public List <Opportunity>    opportunityTransactionList  { get; set; }

    public String   campaignMembersId;
    public String   councilId;
    public decimal  donationPriseAmount;

    private Opportunity membershipOpportunity;
    private Opportunity CouncilMembershipOpp;
    private Opportunity OldDonationopportunity;
    private PricebookEntry donationPricebookEntry;
    private Account councilAccount;
    
    private map<Id, PricebookEntry> priceBookEntryMap;
    private map<String, GSA_payment__c> paymentOptionsMap;

    private String CouncilMembershipOppId;
    private static Integer counterUnableToLockRow = 0;

    private boolean isUpdateDonation = false;
    private Boolean backFlag;
    private Boolean noDelete;

    private static final string GIRL_SCOUTS_USA_PRICEBOOK = 'Girl Scouts USA';
    private static final string DONATION = 'Donation';

    set<String> membershipOppStrIdSet = new set<String>();

    public GirlPaymentProcessingController() {

        OldDonationopportunity = new Opportunity();

        acceptGSPromiseAndLaw = false;
        NotAtThisTime = 0;
        total = 0;
        donationPriseAmount = 0;
        confirmTransactions = false;
        noDelete = false;

        counterUnableToLockRow = 0;

        List<Opportunity> membershipOpportunityList = new List<Opportunity>();

        membershipOpportunity = new Opportunity();
        CouncilMembershipOpp = new Opportunity();
        donationPricebookEntry = new PricebookEntry();
        donationMap = new Map<String, decimal>();
        //opportunityTransactionList = new List<Opportunity>();
        priceBookEntryMap = new map<Id, PricebookEntry>();
        membershipOppStrIdSet = new set<String>();

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
            Girl_RegistrationHeaderController.councilAccount = councilAccount;
        }

        if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            contactId = Apexpages.currentPage().getParameters().get('GirlContactId');

        if(Apexpages.currentPage().getParameters().containsKey('ParentContactId'))
            parentContactId = Apexpages.currentPage().getParameters().get('ParentContactId');

        if(Apexpages.currentPage().getParameters().containsKey('OpportunityId')) {
            opportunityId = Apexpages.currentPage().getParameters().get('OpportunityId');
            membershipOppStrIdSet.add(opportunityId);
        }

        if(Apexpages.currentPage().getParameters().containsKey('CouncilMembershipOppId')) {
            CouncilMembershipOppId = Apexpages.currentPage().getParameters().get('CouncilMembershipOppId');
            membershipOppStrIdSet.add(CouncilMembershipOppId);
        }

        if(Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds'))
            campaignMembersId = Apexpages.currentPage().getParameters().get('CampaignMemberIds');

        if(membershipOppStrIdSet != null && membershipOppStrIdSet.size() > 0) {
            membershipOpportunityList = [
                select Id
                     , Name
                     , OwnerId
                     , rC_Giving__Parent__c
                     , RecordTypeId 
                  from Opportunity 
                 where Id IN : membershipOppStrIdSet
            ];
        }
        if(membershipOpportunityList != null && membershipOpportunityList.size() > 0) {
            for(Opportunity membershipOpp : membershipOpportunityList) {
                if(String.valueOf(membershipOpp.Id) == CouncilMembershipOppId){
                    CouncilMembershipOpp = membershipOpp;
                }
                else if(String.valueOf(membershipOpp.Id) == opportunityId){
                    membershipOpportunity = membershipOpp;
                }
            }
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
        if(!paymentOptionsMap.isEmpty()){
            option1Value = (paymentOptionsMap.containsKey('Donation1')) ? paymentOptionsMap.get('Donation1').amountDonate__c.setScale(2) : null;
            option2Value = (paymentOptionsMap.containsKey('Donation2')) ? paymentOptionsMap.get('Donation2').amountDonate__c.setScale(2) : null;
            option3Value = (paymentOptionsMap.containsKey('Donation3')) ? paymentOptionsMap.get('Donation3').amountDonate__c.setScale(2) : null;
            option4Value = (paymentOptionsMap.containsKey('Donation4')) ? paymentOptionsMap.get('Donation4').amountDonate__c.setScale(2) : null;
            option5Value = (paymentOptionsMap.containsKey('Donation5')) ? paymentOptionsMap.get('Donation5').amountDonate__c.setScale(2) : null;
            option6Value = (paymentOptionsMap.containsKey('Donation6')) ? paymentOptionsMap.get('Donation6').amountDonate__c.setScale(2) : null;
        }

        if(membershipOppStrIdSet != null) {

            List<OpportunityLineItem> opportunityLineItemList = [
                Select UnitPrice
                     , PricebookEntry.UseStandardPrice
                     , PricebookEntry.UnitPrice
                     , PricebookEntry.Product2Id
                     , PricebookEntry.Name
                     , PricebookEntryId
                     , OpportunityId 
                  From OpportunityLineItem  
                 where OpportunityId = :membershipOppStrIdSet
            ];

            if(opportunityLineItemList != null && opportunityLineItemList.size() > 0) {
                for(OpportunityLineItem oppLI : opportunityLineItemList) {
                    if(oppLI != null && oppLI.Id != null) {
                        system.debug('\n Name ===: ' + oppLI.PricebookEntry.Name + '\n Price ===: ' + oppLI.PricebookEntry.UnitPrice);
                        donationMap.put(oppLI.PricebookEntry.Name, oppLI.UnitPrice);//oppLI.PricebookEntry.UnitPrice
                        total = total + oppLI.UnitPrice.setScale(2);
                        donationPriseAmount = total;
                    }
                }
            }
        }
    }

    public boolean pastDate() {
        boolean pastBoolean = true;
        if (Integer.valueOf(expYear) < system.today().year()) {
            pastBoolean = false;
        } else if (Integer.valueOf(expYear) == system.today().year()) {
            if (Integer.valueOf(expMonth) < system.today().month()) {
                 pastBoolean = false;   
            } else {
                pastBoolean = true;     
            }   
        } else {
             pastBoolean = true;    
        }
        return pastBoolean;
    }

    public PageReference createTransactionRecord() {
        Savepoint savepoint = Database.setSavepoint();
        noDelete = true;
        try {
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
            
            total = 0;
            Campaign councilPaymentCampaign;
            Contact[] contactList = [Select LastName, Id, AccountId From Contact where Id = :contactId];
            Contact contact = (!contactList.isEmpty() && contactList.size() > 0) ? contactList[0] : new Contact();

            opportunityTransactionList = new List<Opportunity>();
            if(donationPriseAmount != null)
                total = donationPriseAmount.setScale(2);

            if(amountValue != null && contactId != null) {
                total = total + amountValue;

                if(amountValue == 0) {
                   donationMap.remove('Financial Assistance Donation');
                   return null;
                }

                if(contact != null && contact.Id != null && councilAccount != null && councilAccount.Id != null) {
                    Opportunity donatopnOpp = new Opportunity(
                            Name = 'Financial Assistance Donation'
                          , AccountId = contact.AccountId
                          , CampaignId = councilAccount.Payment_Campaign__c
                          , rC_Giving__Activity_Type__c = 'Donation'
                          , rC_Giving__Reporting_Schedule__c = 'One Payment'
                          , rC_Giving__Giving_Amount__c = amountValue
                          , StageName = 'Open'
                          , CloseDate= system.Today()
                          , RecordTypeId = GirlRegistrationUtilty.getOpportunityRecordTypeId(GirlRegistrationUtilty.OPPORTUNITY_DONATION_RECORDTYPE)
                          , PriceBook2Id = donationPricebookEntry.Pricebook2Id
                    );
                    donatopnOpp.OwnerId = membershipOpportunity.OwnerId;
                    opportunityTransactionList.add(donatopnOpp);
                }

                if(amountValue == 0) {
                   donationMap.remove('Financial Assistance Donation');
                   return null;
                }

               for(Opportunity opportunity : opportunityTransactionList) {
                   String strAmount = String.valueOf(amountValue);
                   Decimal dAmount = Decimal.valueOf(strAmount).setScale(2);
                   donationMap.put(opportunity.Name, dAmount);
               }
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public Pagereference processMyOrder() {

        counterUnableToLockRow++;
        Savepoint savepoint = Database.setSavepoint();

        try {
            set<Id> opportunityIdSet = new set<Id>();
            List<OpportunityLineItem> OpportunityLineItemInsertList = new List<OpportunityLineItem>();
            List<OpportunityContactRole> oppContactRoleToLinsertList = new List<OpportunityContactRole>();
            boolean pastDateBoolean = pastDate();

            if(pastDateBoolean == false)
                return addErrorMessage('Card Expiration Date Cannot be in the past.');

            if(backFlag == true && OldDonationopportunity.Id != null && noDelete == true) {            
                    new WithoutSharing().deleteData(new Sobject[]{OldDonationopportunity});
                    OldDonationopportunity.clear();
                    OldDonationopportunity  =  new Opportunity();
            }     
            if(opportunityTransactionList != null && opportunityTransactionList.size() > 0 && opportunityTransactionList[0].Id == null) {
                
                List<Database.Saveresult> oppSaveResult = Database.insert(opportunityTransactionList);

                if(!oppSaveResult.isEmpty() && oppSaveResult.size() > 0){

                    OldDonationopportunity = opportunityTransactionList[0];

                    // Insert opp contact role
                    OpportunityContactRole opportunityContactRole = new OpportunityContactRole(OpportunityId = oppSaveResult[0].getId(), ContactId = contactId, Role = 'Other', IsPrimary = true);
                    new WithoutSharing().insertData(new Sobject[]{opportunityContactRole});

                    // Insert line item
                    OpportunityLineItem donationLineItem = new OpportunityLineItem();
                    donationLineItem.PricebookEntryId = donationPricebookEntry.Id;
                    donationLineItem.OpportunityId = oppSaveResult[0].getId();
                    donationLineItem.Quantity = 1;
                    donationLineItem.UnitPrice = amountValue;                    
                    new WithoutSharing().insertData(new Sobject[]{donationLineItem});
                }

                for(Opportunity insertedOpp : opportunityTransactionList) {
                    if(insertedOpp != null && insertedOpp.Id != null)
                        opportunityIdSet.add(insertedOpp.Id);
                }

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
                    update OpportunityList;
                    new WithoutSharing().updateData(OpportunityList);
                }
            }

        } catch (System.exception pException) {
            system.debug('Exception ====:  ' + pException.getMessage());
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
            opportunityParentIds.add(membershipOpportunity.Id); // membership
            if(CouncilMembershipOpp != null && CouncilMembershipOpp.Id != null)
                opportunityParentIds.add(CouncilMembershipOpp.Id); // council membership

            system.debug('== opportunityParentIds ====:  ' + opportunityParentIds);
            opportunityParentIds.remove(null);

            opportunityTransactionChargeableList = [
                SELECT Amount
                     , StageName
                     , rC_Giving__Parent__r.CampaignId
                  FROM Opportunity
                 WHERE RecordType.Name = 'Transaction'
                   AND rC_Giving__Parent__c IN :opportunityParentIds
                   FOR UPDATE
            ];
            system.debug('== opportunityTransactionChargeableList ====:  ' + opportunityTransactionChargeableList);
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
                    PaymentServicer_PaypalTransaction.ADDRESS => address,
                    PaymentServicer_PaypalTransaction.ADDR_CITY => city,
                    PaymentServicer_PaypalTransaction.ADDR_STATE => state,
                    PaymentServicer_PaypalTransaction.ADDR_COUNTRY_CODE => 'US',
                    PaymentServicer_PaypalTransaction.ZIPCODE => zipCode,
                    PaymentServicer_PaypalTransaction.TOTAL_AMOUNT => '' + opportunityTransaction.Amount
                }, opportunityTransaction.rC_Giving__Parent__r.CampaignId);

                // Success/failure?
                if (transactionResult.get(PaymentServicer_PaypalTransaction.ISSUCCESS) == 'true') {
                    opportunityTransaction.StageName = 'Completed';
                    sendReciept = true;
                } else {
                    opportunityTransaction.StageName = 'Pending Failed';

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
                paypallog.Name='Girl registration Paypal Response';
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
            if(sendReciept) {
                Contact con = [Select FirstName from Contact where ID=:contactId]; 
                SendReceipt SR = new SendReceipt();
                SR.sendEmail(donationMap,parentContactId,con.FirstName,total);
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
            return null;
        }

        // Done
        List<Contact> parentContactList = [
            SELECT Name, Id
              FROM Contact
             WHERE Id = :parentContactId
        ];

        if (parentContactList.isEmpty() == false) {
            GirlRegistrationUtilty.updateSiteURLAndContactForGirl(''
                + '/Girl_DemographicsInformation'
                + '?GirlContactId='+ contactId
                + '&CouncilId=' + councilId
                + '&CampaignMemberIds=' + campaignMembersId
            , parentContactList[0]);
        }

        PageReference demographicsInfoPage = Page.Girl_DemographicsInformation;
        demographicsInfoPage.getParameters().put('GirlContactId', contactId);
        demographicsInfoPage.getParameters().put('CampaignMemberIds',campaignMembersId);
        demographicsInfoPage.getParameters().put('CouncilId',councilId);
        demographicsInfoPage.getParameters().put('ParentContactId',parentContactId);
        demographicsInfoPage.setRedirect(true);
        return demographicsInfoPage;
    }

    public void updateOpportunityTransactionChargeableList(List<Opportunity> opportunityTransactionChargeableList, Integer failureCount) {
        try {
            if (opportunityTransactionChargeableList.isEmpty() == false) {
                system.debug('opportunityTransactionChargeableList-->'+opportunityTransactionChargeableList);
                update opportunityTransactionChargeableList;
            }
        } catch(System.Exception problem) {
            if(failureCount <= 5)
                updateOpportunityTransactionChargeableList(opportunityTransactionChargeableList, failureCount++);
            else
                throw problem;
        }
    }

    public List<SelectOption> getlistexpYear() {
        List<SelectOption> expOptions = new List<SelectOption>();
        expOptions.add(new Selectoption('--None--', '--None--'));
        for(Integer i=system.today().year(); i < (system.today().year()+12);i++)
            expOptions.add(new SelectOption(i+'',i+' '));
        return expOptions;
    }

    public List<SelectOption> getlistCountryItems() {
        List<SelectOption> countryOptions = new List<SelectOption>();
        map<String, CountryNames__c> countryNamesMap = CountryNames__c.getAll();

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

        if(!stateNamesList.isEmpty() && stateNamesList.size() > 0) {
            for(StateNames__c stateName : stateNamesList)
                stateNamesSortedList.add(stateName.Name);
        }
        stateNamesSortedList.sort();

        stateOptions.add(new SelectOption('--None--', '--None--'));
        for(String stateName : stateNamesSortedList)
            stateOptions.add(new SelectOption(stateName, stateName));
        return stateOptions;
    }

    public PageReference fillPaymentMailingAddress() {
        Savepoint savepoint = Database.setSavepoint();

        try {
            List<Contact> contactList = [
                Select Name
                    , MailingStreet
                    , MailingState
                    , MailingPostalCode
                    , MailingLongitude
                    , MailingLatitude
                    , MailingCountry
                    , MailingCity
                    , Id 
                 From Contact 
                where Id= :contactId
            ];
            Contact contact = (contactList != null && contactList.size() > 0 ) ? contactList[0] : new Contact();

            if(contact != null && contact.Id != null) {
                address = (contact.MailingStreet != null) ? contact.MailingStreet: '';
                city = (contact.MailingCity != null) ? contact.MailingCity : '';
                country = (contact.MailingCountry != null) ? contact.MailingCountry : '';
                if(contact.MailingState != null){
                    String stateName  = GirlRegistrationUtilty.getStateName(contact.MailingState);
                    state =  (stateName != null && stateName != '') ? stateName : contact.MailingState;
                }
                    zipCode = (contact.MailingPostalCode != null) ? contact.MailingPostalCode : '';
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public List<SelectOption> getlistexpMonth() {
        List<SelectOption> expOptions = new List<SelectOption>();
        expOptions.add(new Selectoption('--None--', '--None--'));
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