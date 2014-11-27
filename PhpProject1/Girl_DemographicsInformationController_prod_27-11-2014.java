public with sharing class Girl_DemographicsInformationController extends SobjectExtension{
    
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
    
    public String parentContactId;
    
    public Girl_DemographicsInformationController() {
        campaignMemberIdSet = new set<string>();
        lstAllCampaignFields = new List<SelectOption>();
        lstSelectedCampaignFields= new List<SelectOption>();
         //=========tracking code=============================//
         if(Apexpages.currentPage().getParameters().containsKey('trackid'))
             trackid = Apexpages.currentPage().getParameters().get('trackid');
             Map<String, PublicSiteURL__c> siteUrlMap3 = PublicSiteURL__c.getAll();   
         if(!siteUrlMap3.isEmpty() && siteUrlMap3.ContainsKey('Girl_Registration'))
         customSiteUrl2 = siteUrlMap3.get('Girl_Registration').Volunteer_BaseURL__c;
       //=========tracking code=============================//
        if (Apexpages.currentPage().getParameters().containsKey('ParentContactId'))
            parentContactId = Apexpages.currentPage().getParameters().get('ParentContactId');
        
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
    }
    
    public void getRaceOptionList() {
        Schema.DescribeFieldResult fieldResult = Contact.Race__c.getDescribe();
        List<Schema.PicklistEntry> picklistEntries = fieldResult.getPicklistValues();

        lstAllCampaignFields.add(new SelectOption('--None--', '--None--'));
        if(picklistEntries != null && picklistEntries.size() > 0){
            for(Schema.PicklistEntry picklistValie : picklistEntries)
                lstAllCampaignFields.add(new SelectOption(picklistValie.getValue(), picklistValie.getValue()));
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
             system.debug('lstSelectedCampaignFields = ' + lstSelectedCampaignFields);
           /* system.debug('lstSelectedCampaignFields = ' + lstSelectedCampaignFields);
            if(lstSelectedCampaignFields == null && lstSelectedCampaignFields.size() == 0)
                return addErrorMessage('Please you must select atleast one option');*/
             if(selectedEthnicity.contains('None'))
                 return addErrorMessageAndRollback(savepoint,'Please select Ethinicty');
             if(lstSelectedCampaignFields.size() == 0)   
                 return addErrorMessageAndRollback(savepoint,'Please select atleast one Race option.');
              if(lstSelectedCampaignFields.size() > 1) {
                 String race = '';
                for(system.Selectoption str : lstSelectedCampaignFields) {
                     race += str.getValue() + ';';
                }
                    if(race.contains('None')) 
                    //if(lstSelectedCampaignFields.get(0).getValue().toupperCase().contains('NONE'))
                    return addErrorMessageAndRollback(savepoint, 'None option cannot be selected with multiple values.');
                
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
                    
                    isPrimary = (campaignMember.Primary__c == true) ? true : false;
                    
                    //campaignMember.Active__c = true;
                    campaignMember.Date_Active__c = system.today();

                    if (campaignMember.Campaign.Special_Handling__c == true)
                        isSpecialHandling = true;
                }
                    //remove on 30-10-2014
                //if(isPrimary == false)
                    //campaignMemberList[0].Primary__c = true;
                if(campaignMemberList <> NULL && campaignMemberList.size() > 0)
                    update campaignMemberList; 
                
                List<Contact> parentconactList = [Select Name, Id from Contact where Id = :parentContactId]; 
                Contact parentContact = (parentconactList != null && parentconactList.size() > 0) ? parentconactList[0] : new Contact();
                    
                if(parentContact != null && parentContact.Id != null) {
                 //   GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Girl_DemographicsThankYou' + '?ContactId='+contactId+ '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMemberIds+'&isBackgroundCheckFlag='+ string.ValueOf(isBackgroundCheck)+'&ParentContactId='+parentContact.Id, parentContact);
               
                      //=======================Tracking progress =====================================//
                        if(trackid!=null)
                        {
                           Progress_Tracking__c tracking=[Select Status__c,URL__c,Id from Progress_Tracking__c where Id= :trackid];
                                tracking.URL__c    =customSiteUrl2+ '/Girl_DemographicsThankYou' + '?ContactId='+contactId+ '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMemberIds+'&isBackgroundCheckFlag='+ string.ValueOf(isBackgroundCheck)+'&ParentContactId='+parentContact.Id+'&trackid='+trackid  ;
                                   update tracking;
                        }
                         //=======================Tracking progress =====================================//
                }
                PageReference landingPage = Page.Girl_DemographicsThankYou;//new PageReference('/apex/Girl_DemographicsThankYou');
                landingPage.getParameters().put('isBackgroundCheckFlag', string.ValueOf(isBackgroundCheck));
                landingPage.getParameters().put('GirlContactId', contactId);
                landingPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                landingPage.getParameters().put('CouncilId', councilId);
                landingPage.getParameters().put('ParentContactId', parentContact.Id );
                if(trackid!=null)
                landingPage.getParameters().put('trackid',trackid);
                landingPage.setRedirect(true);
                landingPage.setRedirect(true);
                return landingPage;
            }

            if(campaignMemberList <> NULL && campaignMemberList.size() > 0)
                update campaignMemberList; 

            PageReference defaultRedirectPage = Page.Girl_DemographicsThankYou;//new PageReference('/apex/Girl_DemographicsThankYou');
            defaultRedirectPage.setRedirect(true);
            return defaultRedirectPage;
         } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }            
        return null;
    }
    
    public Contact updateCurrentContact(Contact currentContact) {
        if(currentContact != null) {
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
                , Membership_Status__c
                , StageName
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
              From Contact
             where Id = :contactId
        ];
        return (contactList != null && contactList.size() > 0) ? contactList[0] : null;
    }
}