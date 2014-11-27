public without sharing class Community_Girl_ThankYouPage extends SobjectExtension{
    public Boolean isCashOrCheck {get; set;}
    public Boolean isFinancialAidRequired {get; set;}
    
    private String CouncilId;
    private String CampaignMemberIds = '';
    private String OpportunityId;
    private String contactId;
    private String parentContactId;
    
    public String CouncilName {get; set;}
    public String CouncilMailingAddress {get; set;}
    public String MemberName {get; set;}
    public String SalesforceIdentifier {get; set;}
    public String AmountDue {get; set;}
    public String currentPageUrl { get; set; } 
    public ID trackid;
    public  String customSiteUrl2 ;
    public Account councilAccount;
    public Opportunity memberOpportunity;
    public Contact contact;
    
    public Community_Girl_ThankYouPage() {
        
        councilAccount = new Account();
        memberOpportunity = new Opportunity();
                //=========tracking code=============================//
         if(Apexpages.currentPage().getParameters().containsKey('trackid'))
             trackid = Apexpages.currentPage().getParameters().get('trackid');
          customSiteUrl2  = Label.community_login_URL;
       //=========tracking code=============================//
        if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            contactId = Apexpages.currentPage().getParameters().get('GirlContactId');
        
        if(Apexpages.currentPage().getParameters().containsKey('ParentContactId'))
            parentContactId = Apexpages.currentPage().getParameters().get('ParentContactId');
        
        if(Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            CouncilId = Apexpages.currentPage().getParameters().get('CouncilId');
        
        if(Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds'))
            CampaignMemberIds = Apexpages.currentPage().getParameters().get('CampaignMemberIds');
        
        if(Apexpages.currentPage().getParameters().containsKey('OpportunityId'))
            OpportunityId = Apexpages.currentPage().getParameters().get('OpportunityId');
        
        if(Apexpages.currentPage().getParameters().containsKey('CashOrCheck'))
            isCashOrCheck = Apexpages.currentPage().getParameters().get('CashOrCheck').equalsIgnoreCase('true');
        
        if(Apexpages.currentPage().getParameters().containsKey('FinancialAidRequired'))
            isFinancialAidRequired = Apexpages.currentPage().getParameters().get('FinancialAidRequired').equalsIgnoreCase('true');
            
        councilAccount = getCouncilAccount(CouncilId);
        memberOpportunity = getMemberOpportunity(OpportunityId); 
        
        if(councilAccount != null && memberOpportunity != null){
            if(councilAccount.Name != null && memberOpportunity != null)
                CouncilName = councilAccount.Name;
            if(councilAccount.BillingStreet != null && councilAccount.BillingState  != null && 
               councilAccount.BillingPostalCode != null && councilAccount.BillingCountry != null && councilAccount.BillingCity!= null)
            CouncilMailingAddress = councilAccount.BillingStreet + ', ' + councilAccount.BillingCity + ', ' + councilAccount.BillingState + ', ' + councilAccount.BillingCountry + ', ' + councilAccount.BillingPostalCode;
            system.debug('memberOpportunity.Auto_Giving__c====>'+ memberOpportunity.Auto_Giving__c);
            if(memberOpportunity.Auto_Giving__c != null ){
                SalesforceIdentifier = String.valueOf(memberOpportunity.Auto_Giving__c);
                system.debug('SalesforceIdentifier====>'+ SalesforceIdentifier);
            }
            if(memberOpportunity.rC_Giving__Giving_Amount__c != null){  
                if(councilAccount.Service_Fee__c != null){
                    AmountDue = String.valueOf(memberOpportunity.rC_Giving__Giving_Amount__c + councilAccount.Service_Fee__c);
                }
                else{
                    AmountDue = String.valueOf(memberOpportunity.rC_Giving__Giving_Amount__c);
                }
            }
            
            contact = getContact(memberOpportunity.Contact__c);
            if(contact != null)
                MemberName = contact.Name;
        }    
        system.debug('SalesforceIdentifier====>'+ SalesforceIdentifier);
    }
    
    public Account getCouncilAccount(String CouncilId){
        List<Account> accountList = [
            Select Id
                 , Name
                 , BillingStreet
                 , BillingState
                 , BillingPostalCode
                 , BillingLongitude
                 , BillingLatitude
                 , BillingCountry
                 , BillingCity
                 , Service_Fee__c
              From Account
             Where Id = :CouncilId
        ]; 
        
        return (accountList != null && accountList.size() > 0) ? accountList[0] : null;
    }
    
    public Opportunity getMemberOpportunity(String OpportunityId){
        List<Opportunity> opportunityList = [
            Select Id
                 , Name
                 , rC_Giving__Giving_Amount__c
                 , Contact__c
                 , Auto_Giving__c
                 , Amount
              From Opportunity
             Where Id = :OpportunityId
        ]; 
        
        return (opportunityList != null && opportunityList.size() > 0) ? opportunityList[0] : null;
    }
    
    public Contact getContact(String contactId){
        List<Contact> contactList = [
            Select Id
                 , Name
              From Contact
             Where Id = :contactId
        ]; 
        
        return (contactList != null && contactList.size() > 0) ? contactList[0] : null;
    }
    
    public Pagereference submit() {
        Savepoint savepoint = Database.setSavepoint();
        try{
            List<Contact> contactList = [Select Id, Name, GirlFlowPageURL__c, IsGirlFlowPageDone__c from Contact Where Id = :contactId];
            Contact contact = (contactList != null && contactList.size() > 0) ? contactList[0]: new Contact();
    
           // if(contact != null && contact.Id != null)
             //   GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Community_Girl_DemographicsInformation' + '?GirlContactId='+contactId + '&CouncilId='+CouncilId+'&CampaignMemberIds='+CampaignMemberIds+'&OpportunityId='+OpportunityId, contact);
       //=======================Tracking progress=====================================//
            
                 if(trackid!=null)
                 {
                     if(contact != null && contact.Id != null)
                     customSiteUrl2=customSiteUrl2+'/Community_Girl_DemographicsInformation' + '?GirlContactId='+contactId + '&CouncilId='+CouncilId+'&CampaignMemberIds='+CampaignMemberIds+'&OpportunityId='+OpportunityId    ;
                        Progress_Tracking__c tracking=[Select Status__c,URL__c,Id from Progress_Tracking__c where Id= :trackid];
                         tracking.URL__c    =customSiteUrl2+'&trackid='+trackid;
                            update tracking;
                }
    //=======================Tracking progress=====================================//
            Pagereference paymentProcessingPage = System.Page.Community_Girl_DemographicsInformation;//new Pagereference('/apex/');
            if(CouncilId != null)
                paymentProcessingPage.getParameters().put('CouncilId', CouncilId);
            if(CampaignMemberIds != '')
                paymentProcessingPage.getParameters().put('CampaignMemberIds', CampaignMemberIds);
            if(OpportunityId != null)
                paymentProcessingPage.getParameters().put('OpportunityId', OpportunityId);
            if(contactId != null)
                paymentProcessingPage.getParameters().put('GirlContactId', contactId);
            if(trackid != null)
                paymentProcessingPage.getParameters().put('trackid', trackid);
                
            paymentProcessingPage.setRedirect(true);
            return paymentProcessingPage;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        
    }
    
    public pagereference printPage(){
        
        Pagereference pagereference = Page.Community_Girl_ThankYouPdf;
        pagereference.getParameters().put('ContactId', contactId);
        pagereference.getParameters().put('CouncilId', CouncilId);
        pagereference.getParameters().put('CampaignMemberIds', CampaignMemberIds);
        pagereference.getParameters().put('OpportunityId', OpportunityId);
        pagereference.getParameters().put('CashOrCheck', String.valueOf(isCashOrCheck));
        pagereference.getParameters().put('FinancialAidRequired', String.valueOf(isFinancialAidRequired));
        pagereference.getParameters().put('ParentContactId', parentContactId);
        if(trackid != null)
        pagereference.getParameters().put('trackid', trackid);
         
        system.debug(' '+String.valueOf(pagereference));
        
        List<String> pageReferenceList = String.valueOf(pagereference).split('\\[');
        
        if(!pageReferenceList.isEmpty() && pageReferenceList.size() > 1) {
            currentPageUrl = Url.getSalesforceBaseUrl().toExternalForm() +'/members' +pageReferenceList[1].remove(']');
            system.debug('str############'+currentPageUrl);
        }
        /*
        system.debug('=========>'+currentPageUrl);
        
        if(currentPageUrl != null && currentPageUrl != '' && currentPageUrl.contains('Girl_ThankYou')){
            currentPageUrl = currentPageUrl.replace('Girl_ThankYou', 'Girl_ThankYouPdf');
            
            PageReference pageRef = new PageReference(currentPageUrl);
            system.debug('====pageRef=====>'+pageRef);
            return pageRef;
        }
        */
        return null;
    }
}