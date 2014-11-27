public with sharing class Girl_DemoThankyouPageController  extends SobjectExtension{
  public String isBackgroundCheck;
    public boolean isBackgroundCheckFlag {get;set;}
    
    public String contactId;
    public String campaignMemberIds;
    public String councilId;
    public String parentContactId;
    public List<Contact> girlContactList;
    public List<Account> accountList;
    
    public Girl_DemoThankyouPageController(){
        
        if(Apexpages.currentPage().getParameters().ContainsKey('GirlContactId'))
         contactId = Apexpages.currentPage().getParameters().get('GirlContactId');
         
        if(Apexpages.currentPage().getParameters().ContainsKey('ParentContactId'))
            parentContactId = Apexpages.currentPage().getParameters().get('ParentContactId');
            
        if(Apexpages.currentPage().getParameters().ContainsKey('CampaignMemberIds'))
            campaignMemberIds = Apexpages.currentPage().getParameters().get('CampaignMemberIds');
      
      if(Apexpages.currentPage().getParameters().ContainsKey('isBackgroundCheckFlag'))
            if(Apexpages.currentPage().getParameters().get('isBackgroundCheckFlag') <> NULL)
                isBackgroundCheck = Apexpages.currentPage().getParameters().get('isBackgroundCheckFlag');
        
        if(isBackgroundCheck != null && isBackgroundCheck != ''){
        if(isBackgroundCheck.equals('true')){
            isBackgroundCheckFlag = true;
        }else{
            isBackgroundCheckFlag = false;
        }
    }
    }
    
    public pagereference redirectToVolunteerRegistration() {
        PageReference landingPage = new PageReference(Url.getSalesforceBaseUrl().toExternalForm());//(Label.VolunteerURL);
        if(parentContactId <> NULL && parentContactId <> '') {
            landingPage.getParameters().put('ParentContactId',parentContactId);
        }else {
            return addErrorMessage('Parent Contact Not Found.');
        }
        landingPage.setRedirect(true);
        return landingPage;
    }
    
    public pagereference sendEmailToUser(){
        return null;
    }
}
