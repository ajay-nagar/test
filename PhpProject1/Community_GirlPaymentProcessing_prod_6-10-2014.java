public without sharing class Community_GirlPaymentProcessing extends SobjectExtension {
    public boolean booleanJoinGirlScout { get; set; }
    public boolean booleanBuysGirlScout { get; set; }
    public boolean booleanSupportTenGirls { get;set;}
    public boolean booleanOther { get ;set; }
    public double NotAtThisTime { get; set; }
    public double otherPayment { get; set; }

    public String councilId;
    public String firstName { get; set; }
    public String lastName { get; set; }
    public String address { get; set; }
    public String city { get; set; }
    public String country { get; set; }
    public String state { get; set; }
    public String zipCode { get; set; }
    public String cardHolderName { get; set; }
    public String cardNumber { get; set; }
    public String expMonth { get; set; }
    public String expYear { get; set; }
    public String contactId { get; set;}
    public String opportunityId { get; set;}
    public String opportunityServicefeeId { get; set;}
    public String optionsRadios1 { get; set; }
    public String optionsRadios2 { get; set; }
    public String optionsRadios3 { get; set; }

    public decimal option1Value { get; set; }
    public decimal option2Value { get; set; }
    public decimal option3Value { get; set; }
    public decimal option4Value { get; set; }
    public decimal option5Value { get; set; }
    public decimal option6Value { get; set; }

    public double amountValue { get; set; }
    public decimal total { get; set; }
    public Map<String, decimal> donationMap { get; set; }
    public decimal donationPriseAmount;

    public String securityCode { get; set; }
    public String email { get; set; }
    public String campaignMembersId;

    public String amount { get; set; }
    public boolean acceptGSPromiseAndLaw { get; set; }
    
    public Boolean confirmTransactions { set; get; }
    
    public String primaryContactFullName;
    public String primaryContactEmail;
    
    public List<Opportunity> opportunityTransactionList { get; set;}
    private Zip_Code__c matchingZipCode = new Zip_Code__c();
    private User systemAdminUser;
    private Opportunity OldDonationopportunity;
    private Boolean backFlag;
    private Boolean noDelete;
    private Set<Id> campaignMemberIdSet = new Set<Id>();
    public List<CampaignMember> CampaignMemberList;
    private string oldCampaign = '';
    private CampaignMember oldCampaignMember;
    public Community_GirlPaymentProcessing() {

        OldDonationopportunity =  new Opportunity();
        acceptGSPromiseAndLaw = false;
        NotAtThisTime = 0;
        total = 0;
        donationPriseAmount = 0;
        confirmTransactions = false;
        noDelete = false;
        systemAdminUser = GirlRegistrationUtilty.getSystemAdminUser();
        donationMap = new Map<String, decimal>();
        opportunityTransactionList = new List<Opportunity>();
        CampaignMemberList = new List<CampaignMember>();
        oldCampaignMember = new CampaignMember();
        if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            contactId = Apexpages.currentPage().getParameters().get('GirlContactId');

        if(Apexpages.currentPage().getParameters().containsKey('OpportunityId'))
            opportunityId = Apexpages.currentPage().getParameters().get('OpportunityId');
            
        if(Apexpages.currentPage().getParameters().containsKey('OpportunityServicefeeId'))
            opportunityServicefeeId = Apexpages.currentPage().getParameters().get('OpportunityServicefeeId');

        if(Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds'))
            campaignMembersId = Apexpages.currentPage().getParameters().get('CampaignMemberIds');

        if(Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');

        if(Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(Apexpages.currentPage().getParameters().get('CouncilId'));

        Map<String, GSA_payment__c> paymentOptionsMap = GSA_payment__c.getAll();
        if(!paymentOptionsMap.isEmpty()){
            option1Value = (paymentOptionsMap.containsKey('Donation1')) ? paymentOptionsMap.get('Donation1').amountDonate__c.setScale(2) : null;
            option2Value = (paymentOptionsMap.containsKey('Donation2')) ? paymentOptionsMap.get('Donation2').amountDonate__c.setScale(2) : null;
            option3Value = (paymentOptionsMap.containsKey('Donation3')) ? paymentOptionsMap.get('Donation3').amountDonate__c.setScale(2) : null;
            option4Value = (paymentOptionsMap.containsKey('Donation4')) ? paymentOptionsMap.get('Donation4').amountDonate__c.setScale(2) : null;
            option5Value = (paymentOptionsMap.containsKey('Donation5')) ? paymentOptionsMap.get('Donation5').amountDonate__c.setScale(2) : null;
            option6Value = (paymentOptionsMap.containsKey('Donation6')) ? paymentOptionsMap.get('Donation6').amountDonate__c.setScale(2) : null;
        }

        if(opportunityId != null) {
            List<OpportunityLineItem> opportunityLineItemList = [
                Select PricebookEntry.UseStandardPrice
                     , PricebookEntry.UnitPrice
                     , PricebookEntry.Product2Id
                     , PricebookEntry.Name
                     , PricebookEntryId
                     , OpportunityId 
                  From OpportunityLineItem  
                 where OpportunityId = :opportunityId
            ];
            OpportunityLineItem opportunityLineItem = (opportunityLineItemList != null && opportunityLineItemList.size() > 0) ? opportunityLineItemList[0]: new OpportunityLineItem();

            if(opportunityLineItem != null && opportunityLineItem.Id != null) {
                donationMap.put(opportunityLineItem.PricebookEntry.Name, opportunityLineItem.PricebookEntry.UnitPrice);
                total = opportunityLineItem.PricebookEntry.UnitPrice.setScale(2);
                donationPriseAmount = total;
            }
        }

        if(councilId != null) {
            List<Account> accountList = [
                Select Id
                     , Name
                     , Service_Fee__c
                  From Account  
                 where Id = :councilId
            ];
            Account account = (accountList != null && accountList.size() > 0) ?accountList[0] : new Account();

            if(account != null && account.Id != null && account.Service_Fee__c != null) {
                    donationMap.put('Service Fee', account.Service_Fee__c.setScale(2));
                    total = total + account.Service_Fee__c.setScale(2);
                    donationPriseAmount = total;
            }
        } 
        List<Contact> contactList = [
            Select Id
                 , Name
                 , Email
                 , MailingPostalCode
             From Contact 
            where Id= :contactId
        ];
        Contact contact = (contactList != null && contactList.size() > 0 ) ? contactList[0] : new Contact();

        if(contact != null && contact.Id != null) {                
            zipCode = (contact.MailingPostalCode != null) ? contact.MailingPostalCode : '';
            primaryContactFullName = contact.Name;
            primaryContactEmail = contact.Email;
        }
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
    public PageReference createTransactionRecord() {
        Savepoint savepoint = Database.setSavepoint();
        opportunityTransactionList = new List<Opportunity>();
        noDelete = true;
        try{
            total = 0;
            if(donationPriseAmount != null)
                total = donationPriseAmount.setScale(2);

            if(amountValue != null && contactId != null) {
                decimal temp = 0;
                temp = total + decimal.ValueOf(amountValue).setScale(2);
                total = temp;
                
                if(decimal.ValueOf(amountValue) == 0) {
                   donationMap.remove('Financial Assistance Donation');
                   return null;
                }
                Contact[] contactList = [
                    Select LastName
                         , Id
                         , AccountId 
                      From Contact 
                     where Id = :contactId
                ];
                Contact contact = (!contactList.isEmpty() && contactList.size() > 0) ? contactList[0] : new Contact();
                /* Commented to set campaign from council
                List<Campaign> campaignList = [
                    Select Id
                         , Name 
                      from Campaign 
                     Where Name = '2014 Donations'
                ];
                Campaign campaign = (campaignList != null && campaignList.size() > 0) ? campaignList[0] : new Campaign();
                */
                // Added to set campaign from concil
                Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);
                if(Girl_RegistrationHeaderController.councilAccount.Payment_Campaign__c != null && contact != null && contact.Id != null) {
                    opportunityTransactionList.add(
                        new Opportunity(
                            Name = 'Financial Assistance Donation'
                          , AccountId = contact.AccountId
                          , CampaignId = Girl_RegistrationHeaderController.councilAccount.Payment_Campaign__c
                          , rC_Giving__Activity_Type__c = 'Donation'
                          , rC_Giving__Reporting_Schedule__c = 'One Payment'
                          , rC_Giving__Giving_Amount__c = amountValue
                          , StageName = 'Open'
                          , CloseDate= system.Today()
                          , RecordTypeId = GirlRegistrationUtilty.getOpportunityRecordTypeId(GirlRegistrationUtilty.OPPORTUNITY_DONATION_RECORDTYPE)
                    )
                  );
                }

               String zipCodeToMatch = (zipCode != null && zipCode.length() > 5) ? zipCode.substring(0, 5) + '%' : zipCode + '%'; 
               Zip_Code__c[] zipCodeList = new Zip_Code__c[] {};
                if(zipCode != null && zipCode != '')
                    zipCodeList = [
                        Select Id
                             , Name
                             , Council__c
                             , Zip_Code_Unique__c
                             , City__c
                             , Recruiter__c 
                          From Zip_Code__c 
                         where Zip_Code_Unique__c like :zipCodeToMatch and Recruiter__r.isActive = true limit 1
                    ];
                
                matchingZipCode = (zipCodeList.size() > 0) ? zipCodeList[0] : new Zip_Code__c(); 
               for(Opportunity opportunity : opportunityTransactionList) {
                   donationMap.put(opportunity.Name, decimal.ValueOf(amountValue).setScale(2));
                   if(matchingZipCode != null && matchingZipCode.Recruiter__c != null) {
                    opportunity.OwnerId = matchingZipCode.Recruiter__c;
                    } else {
                    opportunity.OwnerId = systemAdminUser.id;
                    }
                   }
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }
    
    public Pagereference processMyOrder() {
        Savepoint savepoint = Database.setSavepoint();

        try{
        boolean pastDateBoolean = pastDate();
            if(pastDateBoolean == false)
                return addErrorMessage('Card Expiration Date Cannot be in the past.');
            if(Apexpages.currentPage().getParameters().containsKey('donationid')) {
                if(Apexpages.currentPage().getParameters().get('donationid')!=null) {
                Opportunity opp = [Select Id from opportunity where id=:Apexpages.currentPage().getParameters().get('donationid')];
                delete opp;
                }
            }
            if(backFlag == true && OldDonationopportunity.Id != null && noDelete == true) {            
                    Delete OldDonationopportunity;
                    OldDonationopportunity.clear();
                    OldDonationopportunity  =  new Opportunity();
            }     
            if(opportunityTransactionList != null && opportunityTransactionList.size() > 0  && opportunityTransactionList[0].Id == null) {
                List<Database.Saveresult> oppSaveResult = Database.insert(opportunityTransactionList);
                if(!oppSaveResult.isEmpty() && oppSaveResult.size() > 0){
                    OpportunityContactRole opportunityContactRole = new OpportunityContactRole(OpportunityId = oppSaveResult[0].getId(), ContactId = contactId, Role = 'Other', IsPrimary = true);
                    Database.Saveresult oppConRoleSaveResult = Database.insert(opportunityContactRole);
                    OldDonationopportunity = opportunityTransactionList[0];
                    // Add line item for donation opportunity
                    List<PricebookEntry > lstPricebookEntry = [
                    Select Id
                         , Name
                         , Pricebook2.Description
                         , Pricebook2.IsActive
                         , Pricebook2.Name
                         , Pricebook2.Id
                         , Pricebook2Id
                         , Product2Id
                         , UnitPrice 
                      From PricebookEntry 
                     where Name = 'Donation' 
                       and Pricebook2.IsActive = true
                ];
                if(lstPricebookEntry.size()>0) {
                    OpportunityLineItem donationLineItem = new OpportunityLineItem();
                    donationLineItem.PricebookEntryId = lstPricebookEntry[0].Id;
                    donationLineItem.OpportunityId = oppSaveResult[0].getId();
                    donationLineItem.Quantity = 1;
                    donationLineItem.UnitPrice = amountValue;                    
                    insert donationLineItem;
                }   
                }
                if(oppSaveResult[0].getId()!=null){
                Opportunity transactionOpp = [Select Id, OwnerId from Opportunity where rC_Giving__Parent__c = :oppSaveResult[0].getId() and recordtype.Name='Transaction'];
                if(matchingZipCode != null && matchingZipCode.Recruiter__c != null) {
                transactionOpp.OwnerId = matchingZipCode.Recruiter__c;
                } else {
                transactionOpp.OwnerId = systemAdminUser.id;
                }
                update transactionOpp;
                }
            }
            
            if(opportunityId != null && opportunityId != '') {

                if(campaignMembersId != null && campaignMembersId != '') {
                    List<String> campaignMemberIdList = campaignMembersId.trim().split(',');

                    if(campaignMemberIdList != null && campaignMemberIdList.size() > 0){
                        oldCampaign = campaignMemberIdList[campaignMemberIdList.size()-1];
                        for(String campaignMember : campaignMemberIdList)
                            campaignMemberIdSet.add(campaignMember.trim());

                        Boolean isBackgroundCheckNeeded = false;
                        for(Campaign campaign : [Select Id, Name, Background_Check_Needed__c From Campaign where Id IN :campaignMemberIdSet]) {
                            if(campaign.Background_Check_Needed__c)
                                isBackgroundCheckNeeded = true;
                        }
                        oldCampaignMember = [Select Id,Pending_Payment_URL__c from CampaignMember where ID = :oldCampaign]; 
                        if(oldCampaignMember.Pending_Payment_URL__c!='' && !oldCampaignMember.Pending_Payment_URL__c.contains('donationid') && OldDonationopportunity.Id !=null) {
                            oldCampaignMember.Pending_Payment_URL__c = oldCampaignMember.Pending_Payment_URL__c + '&donationid='+OldDonationopportunity.Id;
                            update oldCampaignMember;
                        } else if(oldCampaignMember.Pending_Payment_URL__c!='' && oldCampaignMember.Pending_Payment_URL__c.contains('donationid') && OldDonationopportunity.Id !=null)  {
                            string tempurl = oldCampaignMember.Pending_Payment_URL__c;
                            tempurl = tempurl.substring(0,tempurl.length()-30) + '&donationid='+OldDonationopportunity.Id; 
                            oldCampaignMember.Pending_Payment_URL__c = tempurl;
                            update oldCampaignMember;
                        }  
                        //List<Contact> contactList = [
                        //    Select Id
                        //         , Name
                        //         , GirlFlowPageURL__c
                        //         , IsGirlFlowPageDone__c 
                        //      from Contact 
                        //     Where Id = :contactId
                        //];
                        //Contact contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();

                        //if(contact != null && contact.Id != null)
                        //    GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Community_Girl_DemographicsInformation' + '?GirlContactId='+contactId+ '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMembersId, contact);

                        //PageReference demographicsInfoPage = System.Page.Community_Girl_DemographicsInformation;//new PageReference('/');
                        //demographicsInfoPage.getParameters().put('GirlContactId', contactId);
                        //demographicsInfoPage.getParameters().put('CampaignMemberIds',campaignMembersId);
                        //demographicsInfoPage.getParameters().put('CouncilId',councilId);
                        //demographicsInfoPage.setRedirect(true);
                        //return demographicsInfoPage;
                    }
                }
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        
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
        List<CampaignShare> lstCampaignMemberShare = new List<CampaignShare>();
        List<Opportunity> opportunityTransactionChargeableList = null;
        backFlag = false;
        boolean sendReciept = false;
        try {
            Set<Id> opportunityParentIds = new set<Id>();
            if (opportunityTransactionList != null && opportunityTransactionList.size() != 0) {
                opportunityParentIds.addAll(new Map<Id, Opportunity>(opportunityTransactionList).keySet());
            }
            
            // donation opps
            opportunityParentIds = opportunityParentIds.clone();
            if (opportunityId != null && opportunityId != '') {
                opportunityParentIds.add(opportunityId);
            } // membership
            if (opportunityServicefeeId!= null && opportunityServicefeeId!= '') {
                opportunityParentIds.add(opportunityServicefeeId);
            } // membership
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
            }
            // Done
        List<Contact> contactList = [
            Select Id
                 , Name
                 , FirstName
                 , GirlFlowPageURL__c
                 , IsGirlFlowPageDone__c 
              from Contact 
             Where Id = :contactId
        ];
        Contact contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();
            if(sendReciept) {
                User user = [SELECT id, user.ContactId FROM User WHERE id =: UserInfo.getUserId()]; 
                string contactId = user.ContactId;
                SendReceipt SR = new SendReceipt();
                SR.sendEmail(donationMap,contactId,contact.FirstName,total);
            }                
        } catch(System.Exception problem) {
            return addErrorMessage(problem);
        }
        CampaignMemberList = [
                Select Id
                     , ContactId
                     , Display_Renewal__c
                     , Campaign.Background_Check_Needed__c
                     , CampaignId
                  From CampaignMember
                 where Id IN :campaignMemberIdSet
            ];
            
       // Set permission to current user      
       
        for(CampaignMember campaignMember : CampaignMemberList) {
           campaignMember.Display_Renewal__c = false;
           CampaignShare os = new CampaignShare(CampaignId = campaignMember.campaignid);
           os.CampaignId = campaignMember.campaignid; 
           os.CampaignAccessLevel = 'Read';
           os.UserOrGroupId = UserInfo.getUserId();
           lstCampaignMemberShare.add(os);
        }
        // Update the transactions
        Savepoint savepoint = Database.setSavepoint();

        try {
            if(lstCampaignMemberShare.size()>0)
            //insert lstCampaignMemberShare;
            updateOpportunityTransactionChargeableList(opportunityTransactionChargeableList, 0);
            if(sendReciept) {
            oldCampaignMember.Pending_Payment_URL__c = '';
            update oldCampaignMember;
            }
        } catch(Exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }

        // Errors to date?
        if (ApexPages.hasMessages(ApexPages.Severity.Error)) {
            confirmTransactions = false;
            noDelete = false;
            return null;
        }

        // Done
        List<Contact> contactList = [
            Select Id
                 , Name
                 , GirlFlowPageURL__c
                 , IsGirlFlowPageDone__c 
              from Contact 
             Where Id = :contactId
        ];
        Contact contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();

        if(contact != null && contact.Id != null)
            GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Community_Girl_DemographicsInformation' + '?GirlContactId='+contactId+ '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMembersId, contact);

        PageReference demographicsInfoPage = System.Page.Community_Girl_DemographicsInformation;new PageReference('/');
        demographicsInfoPage.getParameters().put('GirlContactId', contactId);
        demographicsInfoPage.getParameters().put('CampaignMemberIds',campaignMembersId);
        demographicsInfoPage.getParameters().put('CouncilId',councilId);
        demographicsInfoPage.setRedirect(true);
        return demographicsInfoPage;

    }
    
    public void updateOpportunityTransactionChargeableList(List<Opportunity> opportunityTransactionChargeableList, Integer failureCount) {
        try {
            if (opportunityTransactionChargeableList.isEmpty() == false)
                update opportunityTransactionChargeableList;
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
        //countryOptions.add(new Selectoption('--None--', '--None--'));
        countryOptions.add(new SelectOption('USA', 'USA'));
        //countryOptions.add(new SelectOption('INDIA', 'INDIA'));
        //countryOptions.add(new SelectOption('CHINA', 'CHINA'));
        //countryOptions.add(new SelectOption('MEXICO', 'MEXICO'));
        //countryOptions.add(new SelectOption('CANADA', 'CANADA'));
        //countryOptions.add(new SelectOption('OTHER', 'OTHER'));
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
                state = (contact.MailingState != null) ? contact.MailingState : '';
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
}