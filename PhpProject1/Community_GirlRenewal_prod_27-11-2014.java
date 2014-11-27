public without sharing class Community_GirlRenewal{
//Declare variables
    private integer counter=0;  //keeps track of the offset
    private integer list_size=10; //sets the page size or number of rows
    public integer total_size; //used to show user the total size of the list
    public List<CampaignMember> CMtoShow { get; set; }
    public List<Account> account;
    public List<Contact> contacts { get; set; }
    public User user { get; set; }
    public Set<ID> setCon = new Set<ID>();
    public string addOpp { get; set; }
    public string cmpID { get; set; }
    public string cmpCTP { get; set; }
    public string CTP { get; set; }
    public List<WrapperClass> wrapperList{get;set;}
    private Contact con;
    public Community_GirlRenewal() {
        user = [
         SELECT id
              , user.ContactId              
           FROM User
          WHERE id =: UserInfo.getUserId()
        ];          
        con = new contact();
        con = [Select AccountID from contact where ID = :user.ContactId and Account.RecordType.Name = 'Household'];
        if(con.AccountID != NULL){
            contacts = [Select ID from contact where AccountID = :con.AccountID];
        }        
        for(Contact cons:contacts){
            setCon.add(cons.ID);
        }  
        total_size = [Select count() from CampaignMember where Active__c = true and Assignment_Type__c = 'Girl' and Display_Renewal__c = true and ContactID IN:setCon];
        /*
        CMtoShow = [Select ID,Pending_Payment_URL__c,ContactID,Contact.Name,Campaign.Name,Continue_This_Position__c,Membership__r.Grant_Requested__c,Membership__r.Membership_on_Paper__c,Membership__r.StageName from CampaignMember where Active__c = true and Assignment_Type__c = 'Girl' and Display_Renewal__c = true and ContactID IN:setCon];
        system.debug('List Size... ' +CMtoShow.size());        
        wrapperList = new List<WrapperClass>();
        if(CMtoShow.size()>0) {
            for(CampaignMember cm:CMtoShow) {
                WrapperClass w = new WrapperClass(cm);
                wrapperList.add(w);
            }
        }*/
      pagenationRecord();
    }
      /** Pagenation Code **/
     
     public void pagenationRecord(){
     CMtoShow = [Select ID,Pending_Payment_URL__c,ContactID,Contact.Name,Campaign.Name,Continue_This_Position__c,Membership__r.Grant_Requested__c,Membership__r.Membership_on_Paper__c,Membership__r.StageName from CampaignMember where Active__c = true and Assignment_Type__c = 'Girl' and Display_Renewal__c = true and ContactID IN:setCon ORDER BY Contact.Name limit: list_size offset: counter];
        system.debug('List Size... ' +CMtoShow.size());        
        wrapperList = new List<WrapperClass>();
        if(CMtoShow.size()>0) {
            for(CampaignMember cm:CMtoShow) {
                WrapperClass w = new WrapperClass(cm);
                wrapperList.add(w);
            }
        }
     }
     
      public PageReference Beginning() { //user clicked beginning
      counter = 0;
      pagenationRecord();
      return null;
   }
 
   public PageReference Previous() { //user clicked previous button
      counter -= list_size;
      pagenationRecord();
      return null;
   }
 
   public PageReference Next() { //user clicked next button
      counter += list_size;
       pagenationRecord();
      return null;
   }
 
   public PageReference End() { //user clicked end
      counter = total_size - math.mod(total_size, list_size);
     pagenationRecord();
     return null;
   }
 
   public Boolean getDisablePrevious() { 
      //this will disable the previous and beginning buttons
      if (counter>0) return false; else return true;
   }
 
   public Boolean getDisableNext() { //this will disable the next and end buttons
      if (counter + list_size < total_size) return false; else return true;
   }
 
   public Integer getTotal_size() {
      return total_size;
   }
 
   public Integer getPageNumber() {
      return counter/list_size + 1;
   }
 
   public Integer getTotalPages() {
      if (math.mod(total_size, list_size) > 0) {
         return total_size/list_size + 1;
      } else {
         return (total_size/list_size);
      }
   }
   /** Pagenation Code End**/
    
    public class WrapperClass {
        public String girlName{get;set;}
        public String campaignName{get;set;}
        public String campaignMId{get;set;}
        public String contactId{get;set;}
        public Boolean grantRequested{get;set;}
        public Boolean onPaper{get;set;}
        public String stageName{get;set;}
        public String paymentUrl{get;set;}
        public WrapperClass(CampaignMember cm)
        {
            girlName = cm.Contact.Name;
            campaignName = cm.Campaign.Name;
            campaignMId = cm.ID;
            contactId = cm.ContactID;
            grantRequested = cm.Membership__r.Grant_Requested__c;
            onPaper = cm.Membership__r.Membership_on_Paper__c;
            stageName = cm.Membership__r.StageName;
            paymentUrl = cm.Pending_Payment_URL__c;
        }
    }
    public pagereference reNew() {
        PageReference page = null;
        system.debug('addOpp... ' +addOpp);
        system.debug('cmpCTP... ' +cmpCTP);
        system.debug('campaignID... ' +System.currentPagereference().getParameters().get('cmID'));
        system.debug('contactID... ' +System.currentPagereference().getParameters().get('contactID'));
        CampaignMember CM = [Select ID,Membership__c,Pending_Payment_URL__c from CampaignMember where ID = :System.currentPagereference().getParameters().get('cmID')];
        OpportunityLineItem OP = new OpportunityLineItem();
        system.debug('Membership__c... '+CM.Membership__c);
        if(CM.Membership__c != null){
            OP = [Select Product2.rC_Giving__End_Date__c,rC_Giving__Item__c,rC_Giving__Item__r.rC_Giving__End_Date__c from OpportunityLineItem where OpportunityID = :CM.Membership__c order by rC_Giving__Item__r.rC_Giving__End_Date__c desc limit 1];
        }
        system.debug('Product2... '+OP.Product2.rC_Giving__End_Date__c);
        system.debug('rC_Giving__Item__c... '+OP.rC_Giving__Item__c);
        system.debug('rC_Giving__Item__r.rC_Giving__End_Date__c... '+OP.rC_Giving__Item__r.rC_Giving__End_Date__c);
        Contact conMember = new contact();
        conMember = [Select MailingPostalCode from contact where id=:System.currentPagereference().getParameters().get('contactID')];
        List<Zip_Code__c> zip = new List<Zip_Code__c>();
        String zipCodeToMatch = (conMember.MailingPostalCode != null && conMember.MailingPostalCode.length() > 5) ? conMember.MailingPostalCode.substring(0, 5) + '%' : conMember.MailingPostalCode + '%';
        system.debug('zipCodeToMatch... '+zipCodeToMatch);
        zip = [Select Council__c from Zip_Code__c where name like:zipCodeToMatch];
        
        if(CM.Pending_Payment_URL__c !=null) {
           string urlPayment = CM.Pending_Payment_URL__c.substring(4); 
           page = new PageReference(urlPayment);                
           page.setRedirect(false);
           return page;
        }

        if(cmpCTP == 'Yes') {
            if(addOpp == 'Yes'){
            page = System.Page.Community_Girl_TroopOrGroupRoleSearch;
            if(zip.size() > 0 && zip[0].Council__c != null)
            page.getParameters().put('CouncilId', zip[0].Council__c);
            page.getParameters().put('GirlContactId', System.currentPagereference().getParameters().get('contactID'));
            page.getParameters().put('ParentContactId', user.ContactId);
            page.getParameters().put('cmID', System.currentPagereference().getParameters().get('cmID'));
            page.setRedirect(false);
            } else {
            page = System.Page.Community_Girl_JoinMembershipInformation;
            if(zip.size() > 0 && zip[0].Council__c != null)
            page.getParameters().put('CouncilId', zip[0].Council__c);
            page.getParameters().put('GirlContactId', System.currentPagereference().getParameters().get('contactID'));
            page.getParameters().put('ParentContactId', user.ContactId);
            page.getParameters().put('CampaignMemberIds', System.currentPagereference().getParameters().get('cmID'));
            }   
            CM.Continue_This_Position__c = 'Yes';    
            CM.Renewal_Date__c = Date.Today();        
            Update CM;     
        } else if(cmpCTP == 'No but i had like to review other Girl Scout opportunities'){
            
            if(OP.Product2.rC_Giving__End_Date__c!=null)
            CM.End_Date__c = OP.Product2.rC_Giving__End_Date__c;
            CM.Display_Renewal__c = False;
            CM.Continue_This_Position__c = 'No, but i had like to review other Girl Scout opportunities.';            
            Update CM;
            
            page = System.Page.Community_Girl_TroopOrGroupRoleSearch;
            if(zip.size() > 0 && zip[0].Council__c != null)
            page.getParameters().put('CouncilId', zip[0].Council__c);
            page.getParameters().put('GirlContactId', System.currentPagereference().getParameters().get('contactID'));
            page.getParameters().put('ParentContactId', user.ContactId);
            page.setRedirect(false);
        } else {
            if(OP.Product2.rC_Giving__End_Date__c!=null)
            CM.End_Date__c = OP.Product2.rC_Giving__End_Date__c;
            CM.Display_Renewal__c = false;
            CM.Continue_This_Position__c = 'No, my girl will not be continuing with Girl Scouts.';            
            Update CM;
            page = System.Page.Community_GirlThanks;
            page.setRedirect(false);
        }
        con.rC_Bios__Role__c = 'Adult';
        con.Secondary_Role__c = 'Parent';
        con.Girl_Registration__c = true;
        update con;
        return page;    
    }   
}