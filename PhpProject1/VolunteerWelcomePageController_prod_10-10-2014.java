public without sharing class VolunteerWelcomePageController {
    public String objContactId;
    public String strCampaignMemberIds;
    public String selectVideoValue{get;set;}
    public String selectGirlScoutValue{get;set;}
    public String selectVolunteerValue {get;set;}
    public String selectInformationValue {get;set;}
    public String selectStaffValue {get;set;}
    public List<Contact> contactListToUpdate;
    public List<CampaignMember> campaignMemberList;
    public set<String> setCampaignIds; 
    public List<Opportunity> opportunityListToUpdate;
    public List<Account> accountCouncilList;
    public String strVideoUrl{get;set;}
    public String strDefaultVideoUrl = '';
    public String councilId;
    public String userId; 
    public List<CampaignMember> campaignMemberListFromContact;
    public List<Contact> contactList;
    public List<User> userList;
    
    public pagereference init() {
        Site_Maintenance_Setting__c siteMaintenanceSetting = Site_Maintenance_Setting__c.getInstance();
        system.debug('***ment1***'+siteMaintenanceSetting);
        if(siteMaintenanceSetting != null){
            system.debug('***ment***'+siteMaintenanceSetting.Volunteer_Renewal_Maintenance__c);
            if(siteMaintenanceSetting.Volunteer_Renewal_Maintenance__c == true){
                return new pagereference('/InMaintenance');
            }
        }
        if(userInfo.getUserType() == 'Guest') {
            return new pagereference('/login');
        }else if(contactList != null && contactList.size() > 0 && contactList[0].Welcome_Complete__c == false && contactList[0].Secondary_Role__c == 'Volunteer'){
                system.debug('***Welcome***');
                return null;
        }else{
            system.debug('***Home***');
            return new pagereference('/home/home.jsp');
        }
            return null;
    }
    
    public VolunteerWelcomePageController(){
        setCampaignIds = new set<String>();
        contactListToUpdate = new List<Contact>();
        campaignMemberList = new List<CampaignMember>();
        opportunityListToUpdate = new List<Opportunity>();
        accountCouncilList = new List<Account> ();
        campaignMemberListFromContact = new List<CampaignMember>();
        contactList = new List<Contact> ();
        userList = new List<User>();
        
        userId = UserInfo.getUserId() ;
        
        userList = [
            Select contactId
              From User
             Where ID =: userId 
             limit 1];
        system.debug('***userList***'+userList);
        if(userList[0].contactId <> NULL) {
            contactList = VolunteerRenewalUtility.contactandcampaignList(userList[0].contactId);
        system.debug('***contactList***'+contactList[0].CampaignMembers);
        if(contactList <> null && contactList[0].MailingPostalCode <> NULL) {
        List<Zip_Code__c> zipCodeList = [
            Select  Council__r.Welcome_Video_Link__c 
              From Zip_Code__c z 
             Where Zip_Code_Unique__c =: contactList[0].MailingPostalCode 
             ];
        //system.debug('*****'+zipCodeList);
         
        if(!zipCodeList.isEmpty() && zipCodeList[0].Council__r.Welcome_Video_Link__c <> NULL && zipCodeList[0].Council__r.Welcome_Video_Link__c <> '') {
            strVideoUrl = zipCodeList[0].Council__r.Welcome_Video_Link__c;
        }else{
                strVideoUrl = Label.WelcomeVideoUrl;
             }     
            }else{
                strVideoUrl = Label.WelcomeVideoUrl;
            } 
        } else{
            strVideoUrl = Label.WelcomeVideoUrl;
        }   
    }
         //strVideoUrl = Label.WelcomeVideoUrl;
        
    public Pagereference submit(){
        boolean originalValue;
        rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
        objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
        originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
        objCampaignMemberSetting.rC_Event__Disable_All__c = true;
        upsert objCampaignMemberSetting;
        
        opportunityListToUpdate = new List<Opportunity>();
        
        Site_Maintenance_Setting__c siteMaintenanceSetting = Site_Maintenance_Setting__c.getInstance();
        if(siteMaintenanceSetting != null){
            if(siteMaintenanceSetting.Volunteer_Renewal_Maintenance__c == true){
                return new pagereference('/InMaintenance');
            }
        }
        
        if(contactList <> NULL && contactList.size() > 0){
        for(Contact contact : contactList){
            campaignMemberListFromContact.addAll(contact.CampaignMembers);
            }
        }
        system.debug('***campaignMemberListFromContact***'+campaignMemberListFromContact);
        set<String> opportunityIdSet = new  set<String>();
        List<CampaignShare> lstCampaignMemberShare = new List<CampaignShare>();
            boolean isPrimary = false;
            if(campaignMemberListFromContact != NULL && campaignMemberListFromContact.size() > 0) {
                for(CampaignMember campaignMember : campaignMemberListFromContact){
                    campaignMember.Active__c = true;
                    campaignMember.Date_Active__c = system.today();
                    campaignMember.Welcome__c = false;
                    opportunityIdSet.add(campaignMember.Membership__c);
                    if(campaignMember.Primary__c == true)
                        isPrimary = true;
                   CampaignShare os = new CampaignShare(CampaignId = campaignMember.campaignid);
                   os.CampaignId = campaignMember.campaignid; 
                   os.CampaignAccessLevel = 'Read';
                   os.UserOrGroupId = UserInfo.getUserId();
                   lstCampaignMemberShare.add(os);
                }
                if(isPrimary == false) {
                    campaignMemberListFromContact[0].Primary__c = true;
                }
                system.debug('***campaignMemberListFromContact***'+campaignMemberListFromContact);
                for(Opportunity objOpportunity : [Select Id,Membership_Status__c From Opportunity where Id IN: opportunityIdSet]){
                    if(objOpportunity.Id <> NULL){
                        objOpportunity.Membership_Status__c  ='Active';
                        opportunityListToUpdate.add(objOpportunity);
                    }
                }
            }
            if(lstCampaignMemberShare.size()>0)
            insert lstCampaignMemberShare;
        Contact objContact = new Contact();
        objContact.id = userList[0].contactId;
        objContact.Welcome_Q1__c = selectVideoValue;
        objContact.Welcome_Q2__c = selectGirlScoutValue;
        objContact.Welcome_Q3__c = selectVolunteerValue;
        objContact.Welcome_Q4__c = selectInformationValue; 
        objContact.Welcome_Q5__c = selectStaffValue;
        objContact.Welcome_Complete__c = true;
        contactListToUpdate.add(objContact);
            if(contactListToUpdate <> NULL && contactListToUpdate.size() > 0)
                update contactListToUpdate;
            system.debug('contactListToUpdate--->'+contactListToUpdate);
            if(campaignMemberListFromContact <> NULL && campaignMemberListFromContact.size() > 0)
                try{
                    VolunteerRenewalUtility.updateCampaignMemberList(campaignMemberListFromContact);//update campaignMemberListFromContact;
                }catch(Exception ex){} 
            if(opportunityListToUpdate <> NULL && opportunityListToUpdate.size() > 0)
                update opportunityListToUpdate;   
            objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
                update objCampaignMemberSetting;                             
        Pagereference volunteerWelcomePage = new Pagereference('/VolunteerRenewal_AfterSubmitQuestions');
        volunteerWelcomePage.setRedirect(true);
        return volunteerWelcomePage;
    }

    public Pagereference homePage() {
        Pagereference volunteerSubmitPage = new Pagereference('/home/home.jsp');
        system.debug('***volunteerSubmitPage***'+volunteerSubmitPage);
        volunteerSubmitPage.setRedirect(true);
        return volunteerSubmitPage;
    }

}