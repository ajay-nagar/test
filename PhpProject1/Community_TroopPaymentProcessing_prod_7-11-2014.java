public without sharing class Community_TroopPaymentProcessing extends SobjectExtension {
    public string str{get;set;}
    public String campaignMemberIds;
    public set<string> campaignMemberIdSet;
    public set<string> councilIdSet;
    public String opportunityIds;
    public set<string> opportunityIdSet;
   
    
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
     public ID campaign_id4_membership;
     public ID campaign_id4_council;
     public Decimal total_paypalamount4membership; 
     public Decimal total_paypalamount4council; 
     public  String istransaction4council;
     public String istransaction4membership;
    public double amountValue { get; set; }
    public decimal total { get; set; }
    public Map<String, decimal> donationMap { get; set; }
    public decimal donationPriseAmount;
    public decimal MembershipFee ;
    public decimal ServiceFee ;

    public String securityCode { get; set; }
    public String email { get; set; }
    public String campaignMembersId;

    public String amount { get; set; }
    public boolean acceptGSPromiseAndLaw { get; set; }
    public List<Opportunity> opportunityTransactionList { get; set;}
    private Zip_Code__c matchingZipCode = new Zip_Code__c();
    private User systemAdminUser;
    public User user{get;set;}
    public Boolean confirmTransactions { set; get; }
    
    //added by 07-08-2014
    public boolean membershipVariable; 
    public List<CampaignMember> CampaignMemberList;
    private Opportunity membershipOpportunity;
    private PricebookEntry donationPricebookEntry;
    private Account councilAccount;
    private map<Id, PricebookEntry> priceBookEntryMap;
    private static Integer counterUnableToLockRow = 0;
    private static final map<String, Schema.RecordTypeInfo> OPPORTUNITY_RECORDTYPE_INFO_MAP =  Opportunity.SObjectType.getDescribe().getRecordTypeInfosByName();

    Set<ID> myset = new Set<ID>();
    
    Public ID Contact_id;
    public Community_TroopPaymentProcessing() {
     user = [
         SELECT id
              , user.ContactId              
           FROM User
          WHERE id =: UserInfo.getUserId()
        ];  
        
        Contact_id=user.ContactId;
        
        campaignMemberIdSet = new set<string>();
        opportunityIdSet=new set<string>();
        CampaignMemberList = new List<CampaignMember>();

        
        if(Apexpages.currentPage().getParameters().containsKey('OpportunityIds')) {
            opportunityIds = Apexpages.currentPage().getParameters().get('OpportunityIds');
            if (opportunityIds != null && opportunityIds.length() > 0) {
                String[] opportunityIdList = opportunityIds.split(',');
                if (opportunityIdList != null && opportunityIdList.size() > 0) {
                    for(String opprotunityId : opportunityIdList)
                        opportunityIdSet.add(opprotunityId );
                }
            }
        }  
        
        
        if(Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds')) {
            campaignMemberIds = Apexpages.currentPage().getParameters().get('CampaignMemberIds');
            if (campaignMemberIds != null && campaignMemberIds.length() > 0) {
                String[] campaignMemberList = campaignMemberIds.split(',');
                if (campaignMemberList != null && campaignMemberList.size() > 0) {
                    for(String campaignMemberId : campaignMemberList)
                        campaignMemberIdSet.add(campaignMemberId );
                }
            }
        } 
        istransaction4council='';
         istransaction4membership='';
         total_paypalamount4membership =0; 
         total_paypalamount4council=    0; 
         
        acceptGSPromiseAndLaw = false;
        NotAtThisTime = 0;
        total = 0;
        donationPriseAmount = 0;
        confirmTransactions = false;
        systemAdminUser = GirlRegistrationUtilty.getSystemAdminUser();
        donationMap = new Map<String, decimal>();
        opportunityTransactionList = new List<Opportunity>();
        //07-08-2014
        counterUnableToLockRow = 0;
        membershipVariable = false;

        if(opportunityIdSet != null) {
            List<OpportunityLineItem> opportunityLineItemListName = [
                Select PricebookEntry.UseStandardPrice
                     , PricebookEntry.UnitPrice
                     , PricebookEntry.Product2Id
                     , PricebookEntry.Name
                     , PricebookEntryId
                     , OpportunityId 
                     ,TotalPrice
                     ,UnitPrice
                  From OpportunityLineItem  
                 where OpportunityId in : opportunityIdSet ];

             MembershipFee = 0;
            ServiceFee = 0;
           
            for(OpportunityLineItem item1:opportunityLineItemListName  )
            {
               
                if(item1.PricebookEntry.Name.trim().equalsIgnoreCase('council service fee')){
                    ServiceFee = ServiceFee + item1.UnitPrice.setScale(2);
                }
                if(!item1.PricebookEntry.Name.trim().equalsIgnoreCase('council service fee')){
                    MembershipFee = MembershipFee + item1.UnitPrice.setScale(2);
                }
            }
            
            total=ServiceFee +MembershipFee ;
            if(MembershipFee >0){
            donationMap.put('Membership Fee', MembershipFee );
            }
            if(ServiceFee >0){
            donationMap.put('Service Fee', ServiceFee );
            }
            donationPriseAmount = total;
        
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
    
        return null;
    }
    
    
    
    public Pagereference processMyOrder() {
        
        counterUnableToLockRow++;
        Savepoint savepoint = Database.setSavepoint();
        List<Opportunity> opportunityList = new List<Opportunity>();
            boolean pastDateBoolean = pastDate();
            if(pastDateBoolean == false)
                return addErrorMessage('Card Expiration Date Cannot be in the past.');


        counterUnableToLockRow = 0;
        confirmTransactions = true;
        return null;
    }
    
    
    
     public PageReference processPaypalTransactionsUndo() {
        confirmTransactions = false;
        return null;
    }
    
    
    public PageReference processPaypalTransactions() {

        List<Contact> lstParentContact=new List<Contact>();
        total_paypalamount4council=0;
        total_paypalamount4membership=0;
        
        istransaction4membership='false';
        istransaction4council='false';
        
        String GirlsName='';
        ID oppTransaction_id4_membership;
        ID oppTransaction_id4_council;
        String CurrentUser_ContactFullName='';
        String CurrentUser_ContactEmail='';
        boolean sendReciept = false;
        String response_message4membership='';
        String response_message4council='';
        String  transactionid4membership='';
        String  transactionid4council='';
 
        Contact con=[select ID,FirstName,Name,Email,mailingpostalcode,Receipt_for_Troop_Renewal__c from contact where id=:contact_Id limit 1];
        
        if(con!=null)
        {
        CurrentUser_ContactEmail = con.Email;
        CurrentUser_ContactFullName = con.Name;
        }
        
        
     List<CampaignShare> lstCampaignMemberShare = new List<CampaignShare>();
        List<Opportunity> opportunityTransactionChargeableList = null;
        try {
            Set<String> campaignMemberIdSet = new Set<String>();
            Set<Id> opportunityParentIds = new set<Id>();
            if (opportunityTransactionList != null && opportunityTransactionList.size() != 0) {
                opportunityParentIds.addAll(new Map<Id, Opportunity>(opportunityTransactionList).keySet());
            }
             system.debug('opportunityTransactionList=>'+opportunityTransactionList.size());
             system.debug('check campaignMemberIds =>'+campaignMemberIds );
             
            if(campaignMemberIds != null && campaignMemberIds != ''){
                List<String> campaignMemberIdList = campaignMemberIds.trim().split(',');
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
                     , Contact.mailingpostalcode
                     , Contact.Name 
                     ,membership__C
                     ,Parent_First_Name__c
                     ,Girl_First_Name__c
                     ,Campaign.Name
                  From CampaignMember
                 where Id IN :campaignMemberIdSet
            ];
            
           system.debug('campaignMemberIdSet =>'+campaignMemberIdSet );
           system.debug('check CampaignMemberList size =>'+CampaignMemberList.size() );
            for(CampaignMember campaignMember : CampaignMemberList) {
               //campaignMember.Display_Renewal__c = false;
               CampaignShare os = new CampaignShare(CampaignId = campaignMember.campaignid);
               os.CampaignId = campaignMember.campaignid; 
               os.CampaignAccessLevel = 'Read';
               os.UserOrGroupId = UserInfo.getUserId();
               lstCampaignMemberShare.add(os);
               
               if(campaignMember.Contact.Name !=null && campaignMember.Contact.Name !='')
                   GirlsName +='<p>' + campaignMember.Contact.Name +'</p>';
            }
            
            if(opportunityId != null && opportunityId != '') {
                opportunityParentIds.add(opportunityId);
            }
            
             system.debug('check opportunityIdSet size =>'+opportunityIdSet.size() );

            opportunityTransactionChargeableList = [
                SELECT Amount
                     , StageName
                     , rC_Giving__Parent__r.CampaignId
                     , rC_Giving__Parent__c
                     ,rC_Giving__Parent__r.Item_Type__c
                     , rC_Giving__Parent__r.RecordtypeId
                  FROM Opportunity
                 WHERE RecordType.Name = 'Transaction'
                 AND rC_Giving__Parent__c IN :opportunityIdSet

            ];
            system.debug('check opportunityTransactionChargeableList size =>'+opportunityTransactionChargeableList .size() );


            for(Opportunity opportunityTransaction1 : opportunityTransactionChargeableList) {
      
                Boolean isStageOpen = 'Open'.equalsIgnoreCase(opportunityTransaction1.StageName);
                Boolean isStagePendingFailed = 'Pending Failed'.equalsIgnoreCase(opportunityTransaction1.StageName);
                Boolean isStageBlank = null == opportunityTransaction1.StageName;

                if (isStageOpen == false && isStagePendingFailed == false && isStageBlank == false) {
                    continue;
                }

                if (opportunityTransaction1.rC_Giving__Parent__r.CampaignId == null) {
                    continue;
                }

                if (opportunityTransaction1.Amount == null || opportunityTransaction1.Amount == 0) {
                    continue;
                }
                if(opportunityTransaction1.rC_Giving__Parent__r.Item_Type__c=='Membership'){
                 campaign_id4_membership=opportunityTransaction1.rC_Giving__Parent__r.CampaignId;
                 oppTransaction_id4_membership = opportunityTransaction1.rC_Giving__Parent__c;
                 total_paypalamount4membership=total_paypalamount4membership +  opportunityTransaction1.Amount  ;
                 }
                 if(opportunityTransaction1.rC_Giving__Parent__r.Item_Type__c=='council service fee'){
                 campaign_id4_council=opportunityTransaction1.rC_Giving__Parent__r.CampaignId;
                 oppTransaction_id4_council = opportunityTransaction1.rC_Giving__Parent__c;
                 total_paypalamount4council=total_paypalamount4council + opportunityTransaction1.Amount   ;
                 }
                
                
              }
           system.debug('campaign_id4_membership :'+campaign_id4_membership);
           system.debug('total_paypalamount4membership :'+total_paypalamount4membership);
           system.debug('campaign_id4_council :'+campaign_id4_council);
           system.debug('total_paypalamount4council :'+total_paypalamount4council);
           if((total_paypalamount4membership>0) && (campaign_id4_membership!=null || campaign_id4_membership !='') ){
           system.debug('step_1');
           
Map<String, String> transactionResult4membership = new PaymentServicer_PaypalTransaction().processPayment(new Map<String, String> {
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
                    PaymentServicer_PaypalTransaction.TOTAL_AMOUNT => '' + total_paypalamount4membership,
                    
                    //code not in production so hide below 3 lines
                    PaymentServicer_PaypalTransaction.CUSTOM_VAR => CurrentUser_ContactFullName ,
                    PaymentServicer_PaypalTransaction.INVOICE_ID => '' + oppTransaction_id4_membership,
                    PaymentServicer_PaypalTransaction.CONTACT_EMAIL => CurrentUser_ContactEmail 
                }, campaign_id4_membership);
        system.debug('step_2 :'+campaign_id4_membership);
                
        istransaction4membership= transactionResult4membership.get(PaymentServicer_PaypalTransaction.ISSUCCESS);
        system.debug('step_3 :'+istransaction4membership);
         if(istransaction4membership=='true'){}else{
          
           ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, 'Charge failure for membership fee: ' + transactionResult4membership.get(PaymentServicer_PaypalTransaction.RESPONSEMESSAGE)));
          
                     }
       transactionid4membership=transactionResult4membership.get(PaymentServicer_PaypalTransaction.TRANSACTIONID);
        system.debug('step_4 :'+transactionid4membership);
         response_message4membership=transactionResult4membership.get(PaymentServicer_PaypalTransaction.RESPONSEMESSAGE);
         system.debug('step_5 :'+response_message4membership);
                system.debug('istransaction4membership:'+istransaction4membership);  
                }
                
                if((total_paypalamount4council>0) && (campaign_id4_council!=null || campaign_id4_council !='')){
Map<String, String> transactionResult4council = new PaymentServicer_PaypalTransaction().processPayment(new Map<String, String> {
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
                    PaymentServicer_PaypalTransaction.TOTAL_AMOUNT => '' + total_paypalamount4council,
                    
                    //code not in production so hide below 3 lines
                    PaymentServicer_PaypalTransaction.CUSTOM_VAR => CurrentUser_ContactFullName ,
                    PaymentServicer_PaypalTransaction.INVOICE_ID => '' + oppTransaction_id4_council,
                    PaymentServicer_PaypalTransaction.CONTACT_EMAIL => CurrentUser_ContactEmail 
                }, campaign_id4_council);
                 transactionid4council=transactionResult4council.get(PaymentServicer_PaypalTransaction.TRANSACTIONID);
                 response_message4council=transactionResult4council.get(PaymentServicer_PaypalTransaction.RESPONSEMESSAGE);
                istransaction4council=transactionResult4council.get(PaymentServicer_PaypalTransaction.ISSUCCESS);
                system.debug('istransaction4council:'+istransaction4council);
                 if(istransaction4council=='true'){}else{
          
           ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, 'Charge failure for council fee: ' + transactionResult4council.get(PaymentServicer_PaypalTransaction.RESPONSEMESSAGE)));
          
          }
                
                }
                
                        List<PaypalResponseLog__c> lstpaypallog=new List<PaypalResponseLog__c>();
             for(Opportunity opportunityTransaction : opportunityTransactionChargeableList) {
      
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
                if((opportunityTransaction.rC_Giving__Parent__r.Item_Type__c=='Membership')&&(total_paypalamount4membership>0) && (campaign_id4_membership!=null || campaign_id4_membership !='')){
                
                            if(istransaction4membership=='true'){
                                opportunityTransaction.StageName = 'Completed';
                                membershipVariable = true; 
                            }else{
                                opportunityTransaction.StageName = 'Pending Failed';
                            }
                            
                             opportunityTransaction.rC_Connect__Response_Code__c = transactionid4membership;
                             opportunityTransaction.rC_Connect__Response_Date_Time__c = DateTime.now();
                             opportunityTransaction.rC_Connect__Response_Message__c =response_message4membership;


                               

                 }
                 if((opportunityTransaction.rC_Giving__Parent__r.Item_Type__c=='council service fee') &&(total_paypalamount4council>0) && (campaign_id4_council!=null || campaign_id4_council !='')){
                 
                         if(istransaction4council=='true'){
                                opportunityTransaction.StageName = 'Completed';
                         }else{
                                opportunityTransaction.StageName = 'Pending Failed';
                         }
                         
                        opportunityTransaction.rC_Connect__Response_Code__c = transactionid4council;
                        opportunityTransaction.rC_Connect__Response_Date_Time__c = DateTime.now();
                        opportunityTransaction.rC_Connect__Response_Message__c = response_message4council;


                                  
                     
                 }
             }
                 
                                /****************** Track Paypal Reponse Messages Log*******************/
                                PaypalResponseLog__c paypallog=new PaypalResponseLog__c();
                                paypallog.Response_Code__c=transactionid4membership;
                                paypallog.Response_Date_Time__c=DateTime.now();
                                paypallog.Response_Message__c= response_message4membership;
                               // paypallog.Transaction_Opportunity__c=opportunityTransaction.Id;
                                 paypallog.Troop_Volunteer__c=Contact_id ;
                                paypallog.Name='Troop renewal membership Paypal Response';
                                System.debug('Try to Insert data into PaypalResponseLog__c ======' );
                                lstpaypallog.add(paypallog);
                  
                          
                                PaypalResponseLog__c paypallog2=new PaypalResponseLog__c();
                                paypallog2.Response_Code__c=transactionid4council;
                                paypallog2.Response_Date_Time__c=DateTime.now();
                                paypallog2.Response_Message__c= response_message4council;
                               // paypallog2.Transaction_Opportunity__c=opportunityTransaction.Id;
                                paypallog2.Troop_Volunteer__c=Contact_id ;
                                paypallog2.Name='Troop renewal council Paypal Response';
                                lstpaypallog.add(paypallog2);
                            
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

        } catch(System.Exception problem) {
            return addErrorMessage(problem);
        }
                
        // Update the transactions
        Savepoint savepoint = Database.setSavepoint();

        try {
            //if(lstCampaignMemberShare.size()>0)
            //insert lstCampaignMemberShare;
            
            updateOpportunityTransactionChargeableList(opportunityTransactionChargeableList, 0);
            
        } catch(Exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        // Errors to date?
        if (ApexPages.hasMessages(ApexPages.Severity.Error)) {
            confirmTransactions = false;
            return null;
        }
        //if(membershipVariable == true)
            //VolunteerRenewalUtility.updateCampaignMemberList(CampaignMemberList);   
        
         if(istransaction4council=='true' || istransaction4membership=='true'){

                 if(con!=null)
                 {
                 Zip_Code__c[] zipCodeList = new Zip_Code__c[] {};
                    system.debug('zipCode... '+con.mailingpostalcode );
                    String zipCodeToMatch = (con.mailingpostalcode != null && con.mailingpostalcode.length() > 5) ? con.mailingpostalcode.substring(0, 5) + '%' : con.mailingpostalcode + '%';
                    
                    if(zipCodeToMatch != null && zipCodeToMatch != '')
                        zipCodeList = [
                            Select Id
                                 , Name
                                 , Council__r.Council_Header_Url__c
                              From Zip_Code__c 
                             where Zip_Code_Unique__c like :zipCodeToMatch limit 1 ];
                   string sCouncil_Header_Urlc ;   
                    if(zipCodeList.size()>0)
                   sCouncil_Header_Urlc =zipCodeList[0].Council__r.Council_Header_Url__c!=null?zipCodeList[0].Council__r.Council_Header_Url__c:'';
                 
                  string logo = '<div style="padding-left:10px;height:103px;background-color:#00AE58;">';
                    if(sCouncil_Header_Urlc != null && sCouncil_Header_Urlc != '') {
                        logo = logo + '<img src="' + sCouncil_Header_Urlc + '" style="float:left;padding-top:5px;background-color:#00AE58;"/>';
                    } else {
                        logo = logo + '<img src="' + Label.DefaultCouncilLogo + '" style="float:left;padding-top:5px;background-color:#00AE58;"/>';
                    }
                     logo = logo + '</div>';
                     //GirlsName 
                 String ReceiptContent='';
                 
                 
                           system.debug('campaign_id4_membership :'+campaign_id4_membership);
                           system.debug('total_paypalamount4membership :'+total_paypalamount4membership);
                           system.debug('campaign_id4_council :'+campaign_id4_council);
                           system.debug('total_paypalamount4council :'+total_paypalamount4council);
                  
                 
                 ReceiptContent +=logo;
                 ReceiptContent +='<p>Hello ' + con.FirstName+',</p>';
                 ReceiptContent += '<p>Please see list of renewed girl(s) below:</p>';
                 ReceiptContent +=GirlsName ;
                 ReceiptContent += '<p>Thank you for your payment for your Membership, please see your receipt below:</p>';
                 if(total_paypalamount4membership!=null && total_paypalamount4membership>0)
                 ReceiptContent += '<p><span style="text-align:left;">Membership Fee --- </span><span style="font-weight:bold;text-align:right;">$'+total_paypalamount4membership+'</span></p>';
                 if(total_paypalamount4council!=null && total_paypalamount4council>0)
                 ReceiptContent += '<p><span style="text-align:left;">Service Fee --- </span><span style="font-weight:bold;text-align:right;">$'+total_paypalamount4council+'</span></p>';
                 
                 decimal total=(total_paypalamount4membership+total_paypalamount4council);
                 
                 ReceiptContent += '<p><span style="text-align:left;">Total --- </span><span style="font-weight:bold;text-align:right;">$'+total+'</span></p>';
                 
                 try {
                        con.Receipt_for_Troop_Renewal__c= ReceiptContent ;
                        update con;
                    } catch(Exception ex) {
                     System.debug('Exception in Send troop Leader Mail... ' +ex);
                    }
                 
                 
                 }
                 
                 
            PageReference ThankYouPage = System.Page.Community_Troop_ThankYou;
            if(campaignMemberIds != null)   
                ThankYouPage.getParameters().put('CampaignMemberIds',campaignMemberIds);
            if(opportunityIds != null)   
                ThankYouPage.getParameters().put('OpportunityIds',opportunityIds);
            
            ThankYouPage.setRedirect(true);
            return ThankYouPage;
        }else{
        
        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, 'Charge failure ' ));
        confirmTransactions = false;
            return null;
        }
    }
  
    public List<SelectOption> getlistexpYear() {
        List<SelectOption> expOptions = new List<SelectOption>();
        expOptions.add(new Selectoption('--None--', '--None--'));
        for(Integer i=2014;i<2050;i++)
            expOptions.add(new SelectOption(i+'',i+' '));
        return expOptions;
    }

    public List<SelectOption> getlistCountryItems() {
        List<SelectOption> countryOptions = new List<SelectOption>();
        //countryOptions.add(new Selectoption('--None--', '--None--'));
        countryOptions.add(new SelectOption('USA', 'USA'));
        return countryOptions;
    }

    public List<SelectOption> getlistStateItems() {
        List<StateNames__c> stateNamesList = StateNames__c.getAll().values();
        List<String> stateNamesSortedList = new List<String>();
        List<SelectOption> stateOptions = new List<SelectOption>();

        if(!stateNamesList.isEmpty() && stateNamesList.size() > 0) {
            //for(StateNames__c stateName : stateNamesList)
            //    stateNamesSortedList.add(stateName.Name);
        }
        stateNamesSortedList.sort();

        stateOptions.add(new SelectOption('--None--', '--None--'));
        for(String stateName : stateNamesSortedList)
          { // stateOptions.add(new SelectOption(stateName, stateName));
          }
        return stateOptions;
    }

    public PageReference fillPaymentMailingAddress() {
        Savepoint savepoint = Database.setSavepoint();
    this.contactId=user.ContactId;
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
                address='';
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
    
    public PageReference CancelProcess() {
    /*
    List<Opportunity> Opp=[Select Name
                         //, Id
                         , Contact__c
                         , CampaignId
                         , rC_Giving__Parent__r.Id
                         , rC_Giving__Parent__r.OwnerId
                         , rC_Giving__Parent__c
                         , rC_Giving__Giving_Amount__c
                      From Opportunity 
                     where Id IN : opportunityIdSet];
    for(Opportunity op:Opp)
    {
        str  =str +'--'+op.Name;
        delete op;
    }
    */
    return System.Page.Community_MyTroops;
    }
    
    
    
    public void updateOpportunityTransactionChargeableList(List<Opportunity> opportunityTransactionChargeableList, Integer failureCount) {

        
            if (opportunityTransactionChargeableList.isEmpty() == false) {
                update opportunityTransactionChargeableList;
            }
        
    }

}