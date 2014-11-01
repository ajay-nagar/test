public class VolunteerRenewal_PaymentController extends SobjectExtension{
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
    public double total {get;set;}
    public Map<String, decimal> donationMap {get;set;}

    public String securityCode {get;set;}
    public String email {get;set;}
    public decimal donationPriseAmount;
    public String campaignMemberIds;
    public boolean membershipVariable; 
    public String amount {get;set;}
    public boolean acceptGSPromiseAndLaw {get;set;}
    public List<Opportunity> opportunityTransactionList {get; set;}
    public List<CampaignMember> CampaignMemberList;
    private Opportunity membershipOpportunity;
    private Opportunity OldDonationopportunity;
    private PricebookEntry donationPricebookEntry;
    private User systemAdminUser;
    private Account councilAccount;
    private map<Id, PricebookEntry> priceBookEntryMap;
    private static Integer counterUnableToLockRow = 0;

    private boolean backFlag;
    private Boolean noDelete;

    public Boolean confirmTransactions { set; get; }

    private Map<String, GSA_payment__c> paymentOptionsMap;

    private static final map<String, Schema.RecordTypeInfo> OPPORTUNITY_RECORDTYPE_INFO_MAP =  Opportunity.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final string GIRL_SCOUTS_USA_PRICEBOOK = 'Girl Scouts USA';
    private static final string DONATION = 'Donation';
    private static final string MEMBERSHIP = 'Membership'; 
    private static String getOpportunityRecordTypeId(String name) {
       return (OPPORTUNITY_RECORDTYPE_INFO_MAP.get(name) != null) ? OPPORTUNITY_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }

    private string oldCampaign = '';
    private CampaignMember oldCampaignMember;
    public VolunteerRenewal_PaymentController() {
        membershipVariable = false;
        acceptGSPromiseAndLaw = false;
        NotAtThisTime = 0;
        total = 0;
        donationPriseAmount = 0;
        confirmTransactions = false;
        counterUnableToLockRow = 0;

        noDelete = false;
        oldCampaignMember = new CampaignMember();
        donationMap = new Map<String, decimal>();
        //opportunityTransactionList = new List<Opportunity>();
        CampaignMemberList = new List<CampaignMember>();
        membershipOpportunity = new Opportunity();
        OldDonationopportunity  =  new Opportunity();
        donationPricebookEntry = new PricebookEntry();
        priceBookEntryMap = new map<Id, PricebookEntry>();

        

        fillPricebookEntryList();

        for(PricebookEntry varPricebookEntry : priceBookEntryMap.values()) {
            if(varPricebookEntry.Product2.Name.equalsIgnoreCase(DONATION) && varPricebookEntry.Pricebook2.Name.equalsIgnoreCase(GIRL_SCOUTS_USA_PRICEBOOK)) {
                donationPricebookEntry = priceBookEntryMap.get(varPricebookEntry.Id);
                break;
            }
        }

        if (Apexpages.currentPage().getParameters().containsKey('ContactId'))
            contactId = Apexpages.currentPage().getParameters().get('ContactId');

        if (Apexpages.currentPage().getParameters().containsKey('OpportunityId'))
            opportunityId = Apexpages.currentPage().getParameters().get('OpportunityId');

        if (Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds'))
            campaignMemberIds = Apexpages.currentPage().getParameters().get('CampaignMemberIds');

        if (Apexpages.currentPage().getParameters().containsKey('CouncilId')) {
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');
            councilAccount = VolunteerRenewalUtility.getCouncilAccount(councilId);
            VolunteerController.councilAccount = councilAccount;
        }

        paymentOptionsMap = GSA_payment__c.getAll();

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
        Contact contact = VolunteerRenewalUtility.getContact(contactId);
        if(contact!=null) {
        systemAdminUser = [Select Id
                 , LastName
                 , IsActive
                 , Profile.Name
                 , Profile.Id
                 , ProfileId  from User where Id = :contact.Account.OwnerId
               and IsActive = true 
               and UserRoleId != null
            limit 1];
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

            total = 0;
            total = donationPriseAmount;
            
            Zip_Code__c matchingZipCode;
            Id campaignId;
            Contact contact = VolunteerRenewalUtility.getContact(contactId);

            if(amountValue != null && amountValue > 0 && contactId != null && contactId != '') {
                total = total + amountValue;

                if (amountValue == 0) {
                   donationMap.remove('Financial Assistance Donation');
                   return null;
                }

                if(contact != null && contact.MailingPostalCode != null && contact.MailingPostalCode != '') {
                    List<Zip_Code__c> matchingZipCodeList = [
                        Select Id
                             , Name
                             , Council__c
                             , Council__r.Payment_Campaign__c
                             , Zip_Code_Unique__c
                             , City__c
                             , Recruiter__c
                             , Recruiter__r.IsActive
                             , Recruiter__r.UserRoleId
                          From Zip_Code__c
                         where Zip_Code_Unique__c = :contact.MailingPostalCode.substring(0, 5) limit 1
                    ];
                    matchingZipCode = matchingZipCodeList.size() > 0 ? matchingZipCodeList[0] : new Zip_Code__c();
                }

                if(councilAccount != null && councilAccount.Payment_Campaign__c != null)
                    campaignId = councilAccount.Payment_Campaign__c;
                else if(matchingZipCode != null && matchingZipCode.Id != null && matchingZipCode.Council__c != null)
                    campaignId = matchingZipCode.Council__r.Payment_Campaign__c;

                if(campaignId != null && contact != null && contact.Id != null) {
                    opportunityTransactionList.add(
                        new Opportunity(
                              Name = 'Financial Assistance Donation'
                            , AccountId = contact.AccountId
                            , CampaignId = campaignId
                            , rC_Giving__Activity_Type__c = 'Donation'
                            , rC_Giving__Reporting_Schedule__c = 'One Payment'
                            , rC_Giving__Giving_Amount__c = amountValue
                            , StageName = 'Open'
                            , CloseDate= system.Today()
                            , RecordTypeId = VolunteerRenewalUtility.getOpportunityRecordTypeId(VolunteerRenewalUtility.OPPORTUNITY_DONATION_RECORDTYPE)
                            , PriceBook2Id = donationPricebookEntry.Pricebook2Id
                        )
                    );
                }

                if(opportunityTransactionList != null && opportunityTransactionList.size() > 0) {
                    for(Opportunity donationOpp : opportunityTransactionList) {
                        if(matchingZipCode != null && matchingZipCode.Id != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.IsActive && matchingZipCode.Recruiter__r.UserRoleId != null) {
                            donationOpp.ownerId = matchingZipCode.Recruiter__c;
                        }
                        else if(systemAdminUser != null && systemAdminUser.Id != null)
                            donationOpp.ownerId = systemAdminUser.Id;
                    }
                }

                for(Opportunity opportunity : opportunityTransactionList){
                    if(opportunity != null && opportunity.Name != null)
                        donationMap.put(opportunity.Name, amountValue);
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
        List<Opportunity> opportunityList = new List<Opportunity>();
        try {
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
                    new WithoutSharing().deleteData(new Sobject[]{OldDonationopportunity});
                    OldDonationopportunity.clear();
                    OldDonationopportunity  =  new Opportunity();
            }
            if(opportunityTransactionList != null && opportunityTransactionList.size() > 0 && opportunityTransactionList[0].id == null) {

                opportunityList = VolunteerRenewalUtility.insertOpportunityList(opportunityTransactionList);

                set<Id> opportunityIdSet = new set<Id>();

                if(opportunityList != null && opportunityList.size() > 0) {

                    OldDonationopportunity = opportunityList[0];
                    OpportunityContactRole opportunityContactRole = new OpportunityContactRole (
                        OpportunityId = opportunityList[0].Id,
                        ContactId = contactId,
                        Role = 'Other',
                        IsPrimary = true
                    );
                    //OpportunityContactRole opportunityContactRoleNew = VolunteerRenewalUtility.opportunityContactRole(opportunityContactRole);
                    new WithoutSharing().insertData(new Sobject[]{opportunityContactRole});

                    /*************************** Create Line Items ***************************/
                    for(Opportunity opportunity : opportunityList) {
                        opportunityIdSet.add(opportunity.Id);
                    }
                    //Insert opp line item
                    OpportunityLineItem donationLineItem = new OpportunityLineItem();
                    donationLineItem.PricebookEntryId = donationPricebookEntry.Id;
                    donationLineItem.OpportunityId = opportunityList[0].Id;
                    donationLineItem.Quantity = 1;
                    donationLineItem.UnitPrice = amountValue;
                    new WithoutSharing().insertData(new sObject[]{donationLineItem});
                    
                    /*************************** Create Line Items End ***************************/

                    List<Opportunity> transactionOpportunityList = [
                        Select RecordType.Name
                             , RecordTypeId
                             , rC_Giving__Parent__c
                             , rC_Giving__Parent__r.Id
                             , rC_Giving__Parent__r.OwnerId
                          From Opportunity
                         where RecordType.Name = 'Transaction'
                           and rC_Giving__Parent__c IN : opportunityIdSet
                    ];

                    if(transactionOpportunityList.size() > 0) {
                        for(Opportunity opp : transactionOpportunityList) {
                            opp.OwnerId = opp.rC_Giving__Parent__r.OwnerId;
                        }
                        //transactionOpportunityList = VolunteerRenewalUtility.updateOpportunityList(transactionOpportunityList);
                        new WithoutSharing().updateData(transactionOpportunityList);
                    }
                }
            }
        } catch (System.exception pException) {
            if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                if(counterUnableToLockRow <= 3) {
                    Database.rollback(savepoint);
                    processMyOrder();
                }
                else
                    return addErrorMessage('Record is locked by another user. Please re-submit the page once more.');
            }
            else
                return addErrorMessageAndRollback(savepoint, pException);
        }
        if(campaignMemberIds != null && campaignMemberIds != ''){
            List<String> campaignMemberIdList = campaignMemberIds.trim().split(',');
            oldCampaign = campaignMemberIdList[0];
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
        } else if(oldCampaignMember.Pending_Payment_URL__c!='' && oldCampaignMember.Pending_Payment_URL__c.contains('donationid') && OldDonationopportunity.Id ==null)  {
            string tempurl = oldCampaignMember.Pending_Payment_URL__c;
            tempurl = tempurl.substring(0,tempurl.length()-30); 
            oldCampaignMember.Pending_Payment_URL__c = tempurl;
            update oldCampaignMember;
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
        List<CampaignShare> lstCampaignMemberShare = new List<CampaignShare>();
        List<Opportunity> opportunityTransactionChargeableList = null;
        backFlag = false;
        boolean sendReciept = false;
        try {
            Set<String> campaignMemberIdSet = new Set<String>();
            Set<Id> opportunityParentIds = new set<Id>();
            if (opportunityTransactionList != null && opportunityTransactionList.size() != 0) {
                opportunityParentIds.addAll(new Map<Id, Opportunity>(opportunityTransactionList).keySet());
            }
            
            if(campaignMemberIds != null && campaignMemberIds != ''){
                List<String> campaignMemberIdList = campaignMemberIds.trim().split(',');
                oldCampaign = campaignMemberIdList[0];
                for(String campaignMember : campaignMemberIdList){
                    if(campaignMember != null)
                        campaignMemberIdSet.add(campaignMember.trim());
                }
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
            
            // donation opps
            opportunityParentIds = opportunityParentIds.clone();

            if(opportunityId != null && opportunityId != '') {
                opportunityParentIds.add(opportunityId);
            }
            //opportunityParentIds.add(membershipOpportunity.Id); // membership
            opportunityParentIds.remove(null);
            List<CampaignMember> lstCampaignMemberToUpdate;
            opportunityTransactionChargeableList = [
                SELECT Amount
                     , StageName
                     , rC_Giving__Parent__r.CampaignId
                     , rC_Giving__Parent__r.RecordtypeId
                  FROM Opportunity
                 WHERE RecordType.Name = 'Transaction'
                   AND rC_Giving__Parent__c IN :opportunityParentIds
                   FOR UPDATE
            ];
            
            
            for(Opportunity opportunityTransaction : opportunityTransactionChargeableList) {
                //lstCampaignMemberToUpdate.add(opportunityTransaction.Campaign_Members__r);
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
                    if(opportunityTransaction.rC_Giving__Parent__r.RecordtypeId == getOpportunityRecordTypeId(MEMBERSHIP)) {
                        membershipVariable = true;  
                    }
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
            List<Contact> contactList = [
            Select Id
                , Name
                 , VolunteerPage1URL__c
                 , IsVoluntter1stPageDone__c
              from Contact
             Where Id = :contactId
            ];
            Contact contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();
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
            //if(lstCampaignMemberShare.size()>0)
            //insert lstCampaignMemberShare;
            if(sendReciept) {
                
/*************************************************************************************************************/
        //Create  Email template Content for 10,11,12,13,14,15
        
        string sCouncil_Header_Urlc;
        string sService_Feec;
        string sAdult_Contact_First_Namec='';
        
        string sCouncil_Namec='';
        string sCouncil_Addressc='';
        string sContactrName='';
        string sAuto_Givingc='';
        string srC_GivingGiving_Amountc='';
        string sOwnerName='';
        string sOwner_Titlec='';
        string sOwner_Phonec='';
        string sOwner_Emailc='';
        
        string sConFirst_Namec='';
        string sCOwnerName='';
        string sCOwner_Titlec='';
        string sCOwner_Phonec='';
        string sCOwner_Emailc='';

            
            
                
             Opportunity newopp=[select ID
            ,Adult_Contact_First_Name__c
            ,Girl_First_Name__c
            ,Council_Name__c
            ,Council_Address__c
            ,Contact__r.Name
            ,Contact__c
            ,Auto_Giving__c
            ,rC_Giving__Giving_Amount__c
            ,Owner.Name
            ,Owner_Title__c
            ,Owner_Phone__c
            ,Owner_Email__c 
            //,Email_10__c
            //,Email_11__c
            //,Email_12__c
            from Opportunity where Id=:OpportunityId
            limit 1
            ];
            system.debug('Set Email newopp =:'+newopp);    
            Contact newcon=[Select Id
            ,Name
            ,FirstName
            ,MailingPostalCode
            ,Owner_Email__c
            ,Owner_Phone__c
            ,Owner_Title__c
            ,Owner.Name
            from contact where id=:newopp.Contact__c limit 1 ];
            system.debug('Set Email newcon =:'+newcon); 
            string zipcode= (newcon.MailingPostalCode != null && newcon.MailingPostalCode.length() > 5) ? newcon.MailingPostalCode.substring(0, 5) + '%' : newcon.MailingPostalCode + '%';
            Zip_Code__c[] zipCodeList = new Zip_Code__c[] {};
            if(zipCode != null && zipCode != '') {
                zipCodeList = [
                    Select Id
                         , Name
                         , Council__c
                         , Zip_Code_Unique__c
                         , Council__r.Council_Header_Url__c
                         , City__c
                         , Recruiter__c
                         , Recruiter__r.IsActive
                         , Recruiter__r.UserRoleId
                      From Zip_Code__c
                     where Zip_Code_Unique__c like :zipCode limit 1
                ];
            }
            
            if(zipCodeList.size()>0)
            sCouncil_Header_Urlc =zipCodeList[0].Council__r.Council_Header_Url__c!=null?zipCodeList[0].Council__r.Council_Header_Url__c:'';
                            
            sAdult_Contact_First_Namec=newopp.Adult_Contact_First_Name__c!=null?newopp.Adult_Contact_First_Name__c:'';
            
            sCouncil_Namec=newopp.Council_Name__c!=null?newopp.Council_Name__c:'';
            sCouncil_Addressc=newopp.Council_Address__c!=null?newopp.Council_Address__c:'';
            sContactrName=newopp.Contact__r.Name!=null?newopp.Contact__r.Name:'';
            sAuto_Givingc=newopp.Auto_Giving__c!=null?newopp.Auto_Giving__c:'';
            srC_GivingGiving_Amountc=newopp.rC_Giving__Giving_Amount__c!=null?string.valueof(newopp.rC_Giving__Giving_Amount__c):''; //string.valueof(sService_Feec==null?newopp.rC_Giving__Giving_Amount__c:(newopp.rC_Giving__Giving_Amount__c+Decimal.ValueOf(sService_Feec)));
            sOwnerName=newopp.Owner.Name!=null?newopp.Owner.Name:'';
            sOwner_Titlec=newopp.Owner_Title__c!=null?newopp.Owner_Title__c:'';
            sOwner_Phonec=newopp.Owner_Phone__c!=null?newopp.Owner_Phone__c:'';
            sOwner_Emailc=newopp.Owner_Email__c!=null?newopp.Owner_Email__c:'';
            
            
            
            
    string logo = '<div style="padding-left:10px;height:103px;background-color:#00AE58;">';
    if(sCouncil_Header_Urlc != null && sCouncil_Header_Urlc != '') {
        logo = logo + '<img src="' + sCouncil_Header_Urlc + '" style="float:left;padding-top:5px;background-color:#00AE58;"/>';
    } else {
        logo = logo + '<img src="' + Label.DefaultCouncilLogo + '" style="float:left;padding-top:5px;background-color:#00AE58;"/>';
    }
     logo = logo + '</div>';
            string Email10='';
            Email10 +=logo;
            
            
            Email10 +='<p>Hi ' + sAdult_Contact_First_Namec +',</p>';
            Email10 +='<p>Welcome to Girl Scouts as an official member! We know you can’t wait to start the fun in your volunteer role.</p>';            
            Email10 +='<p>The good news is you’re almost ready. We just need you to complete two more simple items - a background check and a short online introduction to the organization.</p>';
            Email10 +='<p>Here’s how the background check will work: You’ll receive an email from our trusted vendor, which conducts background checks on all of our volunteers. Simply follow the instructions and you’ll be on your way.</p>';
            Email10 +='<p>Once your background check has been processed, we will contact you about your online introduction to Girl Scouts.</p>';
            Email10 +='<p>If you have any questions, please reach out. I’m here to help.</p>';
            Email10 +='<p>Have a great day!</P>';
            Email10 +='<p>'+sOwnerName+'<br/>'+sOwner_Titlec+'<br/>'+sOwner_Phonec+'<br/>'+sOwner_Emailc+'</p>';
            system.debug('Email10 ==>'+Email10);
                      
            string Email11='';
            Email11 +=logo;
            
            
            Email11 +='<p>Hi ' + sAdult_Contact_First_Namec +',</p>';
            Email11 +='<p>We know that, as a new member, you can’t wait to get the ball rolling on all the amazing Girl Scout experiences that await you. There’s another step you need to complete before you can become an official Girl Scout volunteer: the background check.</p>';  
            Email11 +='<p>Here’s how the background check will work: you’ll receive an email from our trusted vendor, which conducts background checks on all of our volunteers. Simply follow the instructions and you’ll be on your way.</p>';
            Email11 +='<p>If you have already completed your background check, please disregard this email - it is likely still being processed.We’ll contact you shortly and let you know when that’s done so you can move on to the online introduction to the organization.</P>';
            Email11 +='<p>Got questions? Please don’t hesitate to reach out to me - I’m here to help!</P>';
            Email11 +='<p>Thank you,</p>';
            Email11 +='<p>'+sOwnerName+'<br/>'+sOwner_Titlec+'<br/>'+sOwner_Phonec+'<br/>'+sOwner_Emailc+'</p>';                        
            system.debug('Email11 ==>'+Email11);
            
            string Email12='';
            Email12 +=logo;
            Email12 +='<p>Hi ' + sAdult_Contact_First_Namec +',</p>';
            Email12 +='<p>I just wanted to follow up and see if you have any questions about the background check, which you’ll need to complete before becoming a Girl Scout volunteer.</p>'; 
            Email12 +='<p>Here’s how the background check works: you should have received an email from our trusted vendor, which conducts background checks on all of our volunteers. Once you’ve found that email, simply follow the instructions and you’ll be on your way.</P>';           
            Email12 +='<p>Need help? Please don’t hesitate to get in touch by phone or email. I’m here if you have questions!</p>';
            Email12 +='<p>Once your background check has been processed, we’ll send you more information about your online introduction to Girl Scouts.</P>';
            Email12 +='<p>We look forward to welcoming you soon.</p>';
            Email12 +='<p>Sincerely,</P>';
            Email12 +='<p>'+sOwnerName+'<br/>'+sOwner_Titlec+'<br/>'+sOwner_Phonec+'<br/>'+sOwner_Emailc+'</p>';
    
            system.debug('Email12 ==>'+Email12);
                
                
                newopp.Email_10__c =Email10; 
                newopp.Email_11__c= Email11; 
                newopp.Email_12__c= Email12; 
                
         /*######################Template 13,14,15###############################*/ 
         
            sCOwnerName=newcon.Owner.Name!=null?newcon.Owner.Name:'';
            sCOwner_Titlec=newcon.Owner_Title__c!=null?newcon.Owner_Title__c:'';
            sCOwner_Phonec=newcon.Owner_Phone__c!=null?newcon.Owner_Phone__c:'';
            sCOwner_Emailc=newcon.Owner_Email__c!=null?newcon.Owner_Email__c:'';
            sConFirst_Namec=newcon.FirstName!=null?newcon.FirstName:'';
         
         string Email13='';
            Email13 +=logo;
            
            
            Email13 +='<p>Hi ' + sConFirst_Namec +',</p>';
            Email13 +='<p>Great news! We have processed and approved your background check, and now it’s time for us to tell you a little bit more about the organization!</p>';            
            Email13 +='<p>Now that we’ve gotten the paper work out of the way, <a href="'+Label.community_login_URL+'">complete this short online introduction</a>, where you’ll hear more about Girl Scouts and tools that will help you in your volunteer role.</p>';
            Email13 +='<p>Once you’ve done that, someone will be in touch with the details about your volunteer assignment.</p>';
            
           
            Email13 +='<p>Thank you!</p>';
            Email13 +='<p>'+sCOwnerName+'<br/>'+sCOwner_Titlec+'<br/>'+sCOwner_Phonec+'<br/>'+sCOwner_Emailc+'</p>';
            system.debug('Email13 ==>'+Email13);
                      
            string Email14='';
            Email14 +=logo;
            
            
            Email14 +='<p>Hi ' + sConFirst_Namec+',</p>';
            Email14 +='<p>You’re almost there! Once you <a href="'+Label.community_login_URL+'">complete your online introduction</a> to Girl Scouts, you’ll be ready to get started.</p>';  
            Email14 +='<p>We’ll tell you more about the organization and the tools that we use to help support you in your volunteer role.</p>';
            Email14 +='<p>Have questions? Don’t hesitate to let me know by phone or email. I’m here to help!</p>';
            Email14 +='<p>Once you’ve answered a few quick questions after the introduction, we’ll be in touch with the details about your volunteer placement.</p>';
            Email14 +='<p>Thank you. It’s volunteers like you that make Girl Scouting possible.</p>';
            Email14 +='<p>All the best,</p>';
            Email14 +='<p>'+sCOwnerName+'<br/>'+sCOwner_Titlec+'<br/>'+sCOwner_Phonec+'<br/>'+sCOwner_Emailc+'</p>';
            system.debug('Email14 ==>'+Email14);
            
            string Email15='';
            Email15 +=logo;
            Email15 +='<p>Hi ' + sConFirst_Namec+',</p>';
            Email15 +='<p>Your background check is complete and you’re almost done!</p>'; 
            Email15 +='<p>Last step before all the fun: <a href="'+Label.community_login_URL+'">complete a short online introduction</a> to the organization and answer a couple quick questions. Once you’re done, someone will be in touch with the details about your volunteer placement.</p>';           
            Email15 +='<p>During the introduction, we’ll tell you a little bit more about Girl Scouts and let you know about some tools that will support you as a volunteer.</p>';
            Email15 +='<p>Have questions? Don’t hesitate to let me know by phone or email. I’m here to help.</p>';
            Email15 +='<p>Have a great day!</p>';
            Email15 +='<p>'+sCOwnerName+'<br/>'+sCOwner_Titlec+'<br/>'+sCOwner_Phonec+'<br/>'+sCOwner_Emailc+'</p>';
    
            system.debug('Email15 ==>'+Email15);
                
                
                newopp.Email_13__c =Email13; 
                newopp.Email_14__c= Email14; 
                newopp.Email_15__c= Email15;       
                
         /*#####################################################*/  
              
         update newopp; 
         system.debug('update newopp=:');
/*************************************************************************************************************/   
                
            }
            
            system.debug('opportunityTransactionChargeableList size before update=:'+opportunityTransactionChargeableList.size());
            updateOpportunityTransactionChargeableList(opportunityTransactionChargeableList, 0);
            system.debug('opportunityTransactionChargeableList update Successfully=:');
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
        if(membershipVariable == true)
            VolunteerRenewalUtility.updateCampaignMemberList(CampaignMemberList);   
        // Done
        List<Contact> contactList = [
            Select Id
                 , Name
                 , VolunteerPage1URL__c
                 , IsVoluntter1stPageDone__c
              from Contact
             Where Id = :contactId
        ];
        Contact contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();

        rC_Bios__Contact_Address__c contactAddress = createContactAddress(contact, 'Home', address, city, state, zipCode, country, null);

        if(contact != null && contact.Id != null)
        VolunteerRenewalUtility.updateSiteURLAndContact('VolunteerRenewal_DemographicsInfoPage' + '?ContactId='+contactId + '&CouncilId='+CouncilId+'&CampaignMemberIds='+campaignMemberIds+'&OpportunityId='+opportunityId, contact);

        PageReference demographicsInfoPage = Page.VolunteerRenewal_DemographicsInfoPage;
        if(contactId != null)
            demographicsInfoPage.getParameters().put('ContactId', contactId);
        if(campaignMemberIds != null)
            demographicsInfoPage.getParameters().put('CampaignMemberIds',campaignMemberIds);
        if(councilId != null)
            demographicsInfoPage.getParameters().put('CouncilId',councilId);
        if(opportunityId != null)
            demographicsInfoPage.getParameters().put('OpportunityId',opportunityId);

        demographicsInfoPage.setRedirect(true);
        system.debug('demographicsInfoPage ===> '+demographicsInfoPage);
        return demographicsInfoPage;

    }

    public void updateOpportunityTransactionChargeableList(List<Opportunity> opportunityTransactionChargeableList, Integer failureCount) {
        //try {
        //    if (opportunityTransactionChargeableList.isEmpty() == false)
        //        update opportunityTransactionChargeableList;
        //} catch(System.Exception problem) {
        //    if(failureCount < 5)
        //        updateOpportunityTransactionChargeableList(opportunityTransactionChargeableList, failureCount++);
        //    else
        //        throw problem;
        //}

        try {
            if (opportunityTransactionChargeableList.isEmpty() == false) {
                update opportunityTransactionChargeableList;
            }
        } catch(System.Exception problem) {
            if (problem.getMessage().contains('UNABLE_TO_LOCK_ROW') == false) {
                throw problem; // any errors that are NOT unable to lock row should just be thrown.
            }

            if (failureCount >= 3) {
                throw problem;
            }

            updateOpportunityTransactionChargeableList(opportunityTransactionChargeableList, failureCount++);
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
                    state = contact.MailingState;
                    //state = state.substring(0,1).toUpperCase() + state.substring(1,state.length()).toLowerCase();
                    //state = VolunteerRegistrationUtilty.getStateName(contact.MailingState);
                }

                zipCode = (contact.MailingPostalCode != null) ? contact.MailingPostalCode : '';
            }
        }catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public static String generateUniqueMD5(rC_Bios__Address__c address) {
        String base = ':' + address.rC_Bios__Street_Line_1__c
                    + ':' + address.rC_Bios__Street_Line_2__c
                    + ':' + address.rC_Bios__City__c
                    + ':' + address.rC_Bios__State__c
                    + ':' + address.rC_Bios__Postal_Code__c
                    + ':' + address.rC_Bios__Country__c;
        return EncodingUtil.convertToHex(Crypto.generateDigest('MD5', Blob.valueOf(base.toLowerCase())));
    }

    public rC_Bios__Contact_Address__c createContactAddress(Contact contact, String addrType, String addrStreetLine1, String addrCity,
                                     String addrState, String addrZipCode, String addrCountry, String addrCounty) {
        rC_Bios__Contact_Address__c contactAddress = new rC_Bios__Contact_Address__c();
        List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();
        map <String, rC_Bios__Contact_Address__c> oldContactId_UniqueMD5ToContAddress = new map <String, rC_Bios__Contact_Address__c>();
        map <Id, rC_Bios__Contact_Address__c> contactAddressMap = new map <Id, rC_Bios__Contact_Address__c>();
         
        if(contact != null && contact.Id != null) {

            String addressUniqueKey = '';
            rC_Bios__Address__c address = new rC_Bios__Address__c();

            address.rC_Bios__Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
            address.rC_Bios__City__c = addrCity != null ? addrCity : '';
            address.rC_Bios__State__c = addrState;
            address.rC_Bios__Postal_Code__c = addrZipCode != null && addrZipCode != '' ? addrZipCode.substring(0, 5) : null;
            address.rC_Bios__Country__c = addrCountry != null ? addrCountry : '';

            addressUniqueKey = generateUniqueMD5(address);

            contactAddress.rC_Bios__Contact__c = contact.Id;
            contactAddress.rC_Bios__Type__c = addrType != null ? addrType : '';
            contactAddress.rC_Bios__Original_Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1 != '') ? addrStreetLine1 : null;
            contactAddress.rC_Bios__Original_City__c = (addrCity != null && addrCity != '') ? addrCity : null;
            contactAddress.rC_Bios__Original_State__c = addrState;
            contactAddress.rC_Bios__Original_Postal_Code__c = addrZipCode != null ? addrZipCode : null;
            contactAddress.rC_Bios__Original_Country__c = (addrCountry != null && addrCountry != '') ? addrCountry : null;
            contactAddress.rC_Bios__Preferred_Mailing__c = true;

            String ContactId_UniqueMD5 = contact.Id + addressUniqueKey;

            contactAddressList = [
                Select Id
                     , rC_Bios__Address__r.rC_Bios__Unique_MD5__c
                     , rC_Bios__Preferred_Mailing__c
                     , Contact_Address_UniqueKey__c
                     , rC_Bios__Contact__c
                  From rC_Bios__Contact_Address__c
                 Where rC_Bios__Contact__c = :contact.Id
            ];

            if(contactAddressList != null && contactAddressList.size() > 0){
                for(rC_Bios__Contact_Address__c oldContactAddress : contactAddressList){
                    if(oldContactAddress != null){
                        String oldContactId_UniqueMD5 = oldContactAddress.rC_Bios__Contact__c + oldContactAddress.rC_Bios__Address__r.rC_Bios__Unique_MD5__c;
                        oldContactId_UniqueMD5ToContAddress.put(oldContactId_UniqueMD5, oldContactAddress);
                    }
                }
            }

            try {
                if(oldContactId_UniqueMD5ToContAddress.size() > 0) {
                    if(oldContactId_UniqueMD5ToContAddress.containsKey(ContactId_UniqueMD5)) {
                        rC_Bios__Contact_Address__c existingContactAddress = oldContactId_UniqueMD5ToContAddress.get(ContactId_UniqueMD5);
                        if(existingContactAddress != null && existingContactAddress.rC_Bios__Preferred_Mailing__c == true){
                            //Do nothing
                            system.debug('-------Do nothing--------');
                        }
                        else{
                            for(rC_Bios__Contact_Address__c oldContactAddress : oldContactId_UniqueMD5ToContAddress.values()){
                                if(oldContactAddress.rC_Bios__Preferred_Mailing__c == true){
                                    oldContactAddress.rC_Bios__Preferred_Mailing__c = false;
                                    contactAddressMap.put(oldContactAddress.Id, oldContactAddress);
                                }
                            }
                            existingContactAddress.rC_Bios__Preferred_Mailing__c = true;
                            contactAddressMap.put(existingContactAddress.Id, existingContactAddress);
                            contactAddress = existingContactAddress;
                        }
                        if(contactAddressMap.size() > 0)
                            update contactAddressMap.values();                            
                    }
                    else {
                        for(rC_Bios__Contact_Address__c oldContactAddress : oldContactId_UniqueMD5ToContAddress.values()){
                            if(oldContactAddress.rC_Bios__Preferred_Mailing__c == true){
                                oldContactAddress.rC_Bios__Preferred_Mailing__c = false;
                                contactAddressMap.put(oldContactAddress.Id, oldContactAddress);
                            }
                        }
                        if(contactAddressMap.size() > 0)
                            update contactAddressMap.values();

                        insert contactAddress;
                    }
                    
                }
                else{
                    insert contactAddress;
                }
            } catch(Exception Ex) {
                system.debug('== Address Exception :====>  ' + ex.getMessage());
            }
        }
        return contactAddress;
    }

    public rC_Bios__Contact_Address__c deprecated_createContactAddress(Contact contact, String addrType, String addrStreetLine1, String addrCity,
                                     String addrState, String addrZipCode, String addrCountry, String addrCounty) {
        rC_Bios__Contact_Address__c contactAddress = new rC_Bios__Contact_Address__c();
        system.debug('== contact :==>' + contact);
        List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();

        if(contact != null) {

            contactAddressList = [
                Select Id
                     , rC_Bios__Preferred_Mailing__c
                     , rC_Bios__Preferred_Other__c
                  From rC_Bios__Contact_Address__c
                 Where rC_Bios__Contact__c =: contact.Id
                   and (rC_Bios__Preferred_Mailing__c = true
                    OR rC_Bios__Preferred_Other__c = true)
            ];
            system.debug('== ContactAddressList :====>  ' + contactAddressList);
            if(contactAddressList != null && contactAddressList.size() > 0){
                for(rC_Bios__Contact_Address__c contactAddressNew : contactAddressList){
                    contactAddressNew.rC_Bios__Preferred_Mailing__c = false;
                    contactAddressNew.rC_Bios__Preferred_Other__c = false;
                    updateContactAddressList.add(contactAddressNew);
                }
            }
            system.debug('== updateContactAddressList :====>  ' + updateContactAddressList);

            VolunteerRenewalUtility.updateContactAddress(updateContactAddressList);

            contactAddress.rC_Bios__Contact__c = contact.Id;
            contactAddress.rC_Bios__Type__c = addrType != null ? addrType : '';
            contactAddress.rC_Bios__Original_Street_Line_1__c = addrStreetLine1 != null ? addrStreetLine1 : '';
            //contactAddress.rC_Bios__Original_Street_Line_2__c = addrStreetLine2 != null ? addrStreetLine2 : '';
            contactAddress.rC_Bios__Original_City__c = addrCity != null ? addrCity : '';

            //contactAddress.rC_Bios__Original_State__c = addrState != null ? addrState: '';

            if(addrState != null){
                String stateName = VolunteerRenewalUtility.getStateName(addrState);
                contactAddress.rC_Bios__Original_State__c = (stateName != null && stateName != '') ? stateName : addrState;
            }


            contactAddress.rC_Bios__Original_Postal_Code__c = addrZipCode != null ? addrZipCode : '';
            contactAddress.rC_Bios__Original_Country__c = addrCountry != null ? addrCountry : '';
            contactAddress.rC_Bios__Preferred_Mailing__c = true;
            contactAddress.rC_Bios__Preferred_Other__c = true;

            /*contactAddress.rC_Bios__Seasonal_Start_Day__c = '01';
            contactAddress.rC_Bios__Seasonal_End_Day__c = '30';
            contactAddress.rC_Bios__Seasonal_Start_Month__c = '06';
            contactAddress.rC_Bios__Seasonal_End_Month__c = '08';*/

            try {
            contactAddress = VolunteerRenewalUtility.insertContactAddress(contactAddress);
            } catch(Exception Ex) {
                system.debug('## Exception : inserting duplicate contact address.');
            }
            //updateContactAddressList.add(contactAddress);
            //VolunteerRenewalUtility.updateContactAddress(updateContactAddressList);
        }
        system.debug('==: Inserted child Contact Address ==: ' + contactAddress);
        return contactAddress;
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