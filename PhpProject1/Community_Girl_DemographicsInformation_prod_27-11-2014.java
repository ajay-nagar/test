public without sharing class Community_Girl_DemographicsInformation extends SobjectExtension{
    
    public String selectedRace { get; set; }
    public String selectedEthnicity { get; set; }
    public String GirlNoOfYearsInGS { get; set; }
    public String contactId;
    public String campaignMemberIds;
    public String councilId;
    public String opportunityId;
    public Contact currentContact;
    public Opportunity currentOpportunity;
    public set<string> campaignMemberIdSet;
      public ID trackid;
    public  String customSiteUrl2 ;
    public List<SelectOption> lstAllCampaignFields { get; set; }
    public List<SelectOption> lstSelectedCampaignFields { get; set; }
    
    public Community_Girl_DemographicsInformation() {
        campaignMemberIdSet = new set<string>();
        lstAllCampaignFields = new List<SelectOption>();
        lstSelectedCampaignFields= new List<SelectOption>();
          //=========tracking code=============================//
         if(Apexpages.currentPage().getParameters().containsKey('trackid'))
             trackid = Apexpages.currentPage().getParameters().get('trackid');
          customSiteUrl2  = Label.community_login_URL;
       //=========tracking code=============================//
        if (Apexpages.currentPage().getParameters().containsKey('CouncilId')) {
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');
             Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(Apexpages.currentPage().getParameters().get('CouncilId'));
        }
        if(Apexpages.currentPage().getParameters().get('OpportunityId') <> NULL)
            opportunityId = Apexpages.currentPage().getParameters().get('OpportunityId');
        if(Apexpages.currentPage().getParameters().get('GirlContactId') <> NULL) {
            contactId = Apexpages.currentPage().getParameters().get('GirlContactId');
            currentContact = getContact(contactId);
        }
        if(Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds')) {
            campaignMemberIds = Apexpages.currentPage().getParameters().get('CampaignMemberIds');
            if (campaignMemberIds != null && campaignMemberIds.length() > 0) {
                String[] campaignMemberIdList = campaignMemberIds.split(',');
                if (campaignMemberIdList != null && campaignMemberIdList.size() > 0) {
                    for(String campaignMemberId : campaignMemberIdList)
                        campaignMemberIdSet.add(campaignMemberId);
                }
            }
        }
        getRaceOptionList();
        if(currentContact.of_Adult_Years__c != null){
            GirlNoOfYearsInGS =  string.valueof(currentContact.of_Adult_Years__c + 1);
        } else {
            GirlNoOfYearsInGS =  string.valueof(1);
        }
        if(currentContact.rC_Bios__Ethnicity__c != null)
        selectedEthnicity = currentContact.rC_Bios__Ethnicity__c;
        String[] selectedCampaignFields = (currentContact != null && currentContact.Race__c != null) ? currentContact.Race__c.trim().split(';') : new String[]{};
        for(String scf:selectedCampaignFields) {
           lstSelectedCampaignFields.add(new SelectOption(scf,scf));           
        }        
    }
    
    public void getRaceOptionList() {
        Schema.DescribeFieldResult fieldResult = Contact.Race__c.getDescribe();
        List<Schema.PicklistEntry> picklistEntries = fieldResult.getPicklistValues();
        String[] selectedCampaignFields = (currentContact != null && currentContact.Race__c != null) ? currentContact.Race__c.trim().split(';') : new String[]{};
        set<string> setValues =  new set<string>();
        
        if(picklistEntries != null && picklistEntries.size() > 0){            
            for(Schema.PicklistEntry picklistValie : picklistEntries) {  
            setValues.add(picklistValie.getValue());                                  
            }
        } 
        for(string scv:selectedCampaignFields) {
        setValues.Remove(scv);
        }
        system.debug('selectedCampaignFields... '+selectedCampaignFields);
        system.debug('setValues... '+setValues);
        for(String sv:setValues) {
            lstAllCampaignFields.add(new SelectOption(sv, sv));
        }
    }
    
    public List<SelectOption> getEthnicityOptionList() {
        List<SelectOption> ethnicityOptions = new List<SelectOption>();
        Schema.DescribeFieldResult fieldResult = Contact.rC_Bios__Ethnicity__c.getDescribe();
        List<Schema.PicklistEntry> picklistEntries = fieldResult.getPicklistValues();

        ethnicityOptions.add(new SelectOption('--None--', '--None--'));
        if(picklistEntries != null && picklistEntries.size() > 0){
            for(Schema.PicklistEntry picklistValie : picklistEntries)
                ethnicityOptions.add(new SelectOption(picklistValie.getValue(), picklistValie.getValue()));
        }
        return ethnicityOptions;
    }
    
    public pagereference submit() {
        Savepoint savepoint = Database.setSavepoint();
       
        try {
            boolean isBackgroundCheck = false;
            boolean isSpecialHandling = false;
            boolean isPrimary = false;
            CampaignMember[] campaignMemberList;
            if(lstSelectedCampaignFields != null && lstSelectedCampaignFields.size() > 0){
            }else{
             ApexPages.Message msg = new ApexPages.Message(ApexPages.Severity.Error,'Select at least one race.');
             ApexPages.addMessage(msg); 
             return null;  
            }

            if(currentContact != null)
                currentContact = updateCurrentContact(currentContact);
             
            if(opportunityId != null)
                currentOpportunity = updateCurrentOpportunity(opportunityId);
            
            if(currentOpportunity <> NULL && currentOpportunity.Id != null && currentOpportunity.StageName.equalsIgnoreCase('Completed')) {
                currentOpportunity.Membership_Status__c = 'Active';
                update currentOpportunity;
            }

            if(campaignMemberIdSet != null)
                campaignMemberList = getCampaignMembers(campaignMemberIdSet);

            if (campaignMemberList != null && campaignMemberList.size() > 0) {

                for(CampaignMember campaignMember : campaignMemberList) {
                    if (campaignMember.Campaign.Background_Check_Needed__c == true)
                        isBackgroundCheck = true;
                   
                    if (campaignMember.Campaign.Special_Handling__c == true)
                        isSpecialHandling = true;
                }

                if(campaignMemberList <> NULL && campaignMemberList.size() > 0)
                    update campaignMemberList; 
                    
              //  if(currentContact != null && currentContact.Id != null)
               //     GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Community_Girl_DemographicsThankYou' + '?ContactId='+contactId+ '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMemberIds+'&isBackgroundCheckFlag='+ string.ValueOf(isBackgroundCheck), currentContact);
                  //=======================Tracking progress=====================================//
            
                 if(trackid!=null)
                 {
                      if(currentContact != null && currentContact.Id != null)
                     customSiteUrl2=customSiteUrl2+'/Community_Girl_DemographicsThankYou' + '?ContactId='+contactId+ '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMemberIds+'&isBackgroundCheckFlag='+ string.ValueOf(isBackgroundCheck)    ;
                        Progress_Tracking__c tracking=[Select Status__c,URL__c,Id from Progress_Tracking__c where Id= :trackid];
                         tracking.URL__c    =customSiteUrl2+'&trackid='+trackid;
                            update tracking;
                }
    //=======================Tracking progress=====================================//
                PageReference landingPage = System.Page.Community_Girl_DemographicsThankYou;//new PageReference('/apex/');
                landingPage.getParameters().put('isBackgroundCheckFlag', string.ValueOf(isBackgroundCheck));
                landingPage.getParameters().put('GirlContactId', contactId);
                landingPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                landingPage.getParameters().put('CouncilId', councilId);
                if(trackid != null)
                landingPage.getParameters().put('trackid', trackid);
                landingPage.setRedirect(true);
                landingPage.setRedirect(true);
                return landingPage;
            }

            if(campaignMemberList <> NULL && campaignMemberList.size() > 0)
                update campaignMemberList; 

            PageReference defaultRedirectPage = System.Page.Girl_DemographicsThankYou;//new PageReference('/apex/');
            defaultRedirectPage.setRedirect(true);
            return defaultRedirectPage;
         } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }            
        return null;
    }
    
    public Contact updateCurrentContact(Contact currentContact) {
        system.debug('ofGirlYear... '+currentContact.of_Adult_Years__c);
        if(currentContact != null) {
            system.debug('CampaigngField=>'+lstSelectedCampaignFields);
            if(lstSelectedCampaignFields != null && lstSelectedCampaignFields.size() > 0){
                String race = '';
                for(System.SelectOption raceOption : lstSelectedCampaignFields)
                    race += raceOption.getValue() + ';';
                  
                currentContact.Race__c = race;
            }

            if(selectedEthnicity != null && selectedEthnicity != '' && !selectedEthnicity.toUpperCase().contains('NONE')) 
                currentContact.rC_Bios__Ethnicity__c = selectedEthnicity;
            currentContact.Join_Complete__c = true;

            if(GirlNoOfYearsInGS != null && GirlNoOfYearsInGS != '') {
                currentContact.of_Girl_Years__c = decimal.valueOf((GirlNoOfYearsInGS.length() <= 2) ? GirlNoOfYearsInGS : GirlNoOfYearsInGS.substring(0,2));
            }
            if(GirlNoOfYearsInGS != null && GirlNoOfYearsInGS != '') {
                currentContact.of_Adult_Years__c = decimal.valueOf((GirlNoOfYearsInGS.length() <= 2) ? GirlNoOfYearsInGS : GirlNoOfYearsInGS.substring(0,2));
            }
            system.debug('ofGirlYear... '+currentContact.of_Adult_Years__c);
            update currentContact;
        }
        return currentContact;
    }
    

    public CampaignMember[] getCampaignMembers(set<String> campaignMemberIdSet) {
        CampaignMember[] campaignMemberList = [
            Select Id
                 , ContactId
                 , Membership__c
                 , Membership_Status__c
                 , CampaignId
                 , Primary__c
                 , Campaign.Name
                 , Campaign.Type
                 , Campaign.Background_Check_Needed__c
                 , Campaign.Special_Handling__c
              From CampaignMember
             where Id IN :campaignMemberIdSet
             order By CreatedDate asc
        ];

        return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList : null;
    }
    
    public Opportunity updateCurrentOpportunity(String OpportunityId) {
        Opportunity[] opportunityList = [
            Select Id
                , Membership_Status__c, StageName
            From Opportunity
            Where ID =: OpportunityId
        ];
        return (opportunityList != null && opportunityList.size() > 0) ? opportunityList[0] : new Opportunity();
    } 
    
    public Contact getContact(String contactId) {
        Contact[] contactList = [
            Select Id
                 , Race__c
                 , Occupation__c
                 , rC_Bios__Ethnicity__c
                 //, Employer__c
                 , AccountId
                 , LastName
                 , FirstName
                 , Email
                 , of_Adult_Years__c
              From Contact
             where Id = :contactId
        ];
        return (contactList != null && contactList.size() > 0) ? contactList[0] : null;
    }
}