@isTest 
private class rC_GSATests_00100 {
    
    static testMethod void test_100_SearchForTroopRoleByName() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;    
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('Troop Group',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        insert troopOrGroupVolunteerProject;
        
        Campaign[] campaignList = [Select Id from Campaign where Id = :troopOrGroupVolunteerProject.Id];
        
        Id parentCampaignId =  (campaignList != null && campaignList.size() > 0) ? campaignList[0].Id : '';
        
        Campaign troopGroupVolunteerJob = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob.Display_on_Website__c = true;
        insert troopGroupVolunteerJob;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, troopGroupVolunteerJob.Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
        
        Test.startTest();
        //system.runAs(portalUser) {
            Pagereference TroopGroupRoleSearchPage = Page.VolunteerRenewal_TroopGroupRoleSearch;//new Pagereference('/apex/VolunteerRenewal_TroopGroupRoleSearch');
            TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
            TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
            TroopGroupRoleSearchPage.getParameters().put('CampaignMemberIds',campaignMember.Id);
            Test.setCurrentPage(TroopGroupRoleSearchPage);
            
            troopGroupVolunteerJob.Display_on_Website__c = true;
            update troopGroupVolunteerJob;
        
            VolunteerRenewal_RoleSearchController volunteerRenewalTroopGroupRoleSearch = new VolunteerRenewal_RoleSearchController();
            volunteerRenewalTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
            volunteerRenewalTroopGroupRoleSearch.selectedRadius = '5';
            
            volunteerRenewalTroopGroupRoleSearch.getPageSizeOptions();
            volunteerRenewalTroopGroupRoleSearch.getRadiusInMiles();
            
            volunteerRenewalTroopGroupRoleSearch.selectedPageSize = '1';
            volunteerRenewalTroopGroupRoleSearch.selectedPageNumber = '1';
            
            volunteerRenewalTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
            
            volunteerRenewalTroopGroupRoleSearch.campaignDetailsId = troopGroupVolunteerJob.Id;
            volunteerRenewalTroopGroupRoleSearch.showDetails();
            
            for(Integer i = 0; i < volunteerRenewalTroopGroupRoleSearch.parentCampaignWrapperList.size(); i++){
                if(volunteerRenewalTroopGroupRoleSearch.parentCampaignWrapperList[i].childCampaignName != 'Unsure')
                    volunteerRenewalTroopGroupRoleSearch.parentCampaignWrapperList[i].isCampaignChecked = true;
            }
            
            volunteerRenewalTroopGroupRoleSearch.addCampaignMember();
            
            system.assert(volunteerRenewalTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
            system.assertEquals(volunteerRenewalTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked, true);
            Contact contactNew = VolunteerRegistrationUtilty.contactRecord(contact.Id);
            Campaign campaignNew = VolunteerRegistrationUtilty.campaignRecord(volunteerRenewalTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId); 
            CampaignMember campaignMemberToget = VolunteerRegistrationUtilty.campaignMemberRecord(contactNew.Id ,campaignNew.Id);
            system.assert(campaignMemberToget.Id != null);
            Test.stopTest();
       // }
    }
 
    static testMethod void test_100_SearchForTroopRoleByZipCode() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        List<Zip_Code__c> zipCodeList = new List<Zip_Code__c>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.MailingPostalCode = '192';
        insert contact;
        
        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '192';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        zipCodeList.add(zipCodeInstance1);
        
        Zip_Code__c zipCodeInstance2 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance2.Zip_Code_Unique__c = '19373';
        zipCodeInstance2.name = '19373';
        zipCodeInstance2.geo_location__Latitude__s =  39.89000;
        zipCodeInstance2.geo_location__Longitude__s = -75.53000;
        zipCodeList.add(zipCodeInstance2);
        
        Zip_Code__c zipCodeInstance3 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance3.Zip_Code_Unique__c = '19409';
        zipCodeInstance3.name = '19409';
        zipCodeInstance3.geo_location__Latitude__s =  40.14000;
        zipCodeInstance3.geo_location__Longitude__s = -75.36000;
        zipCodeList.add(zipCodeInstance3);
        insert zipCodeList;
        
        Zip_Code__c zipCodeInstance4 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance4.Zip_Code_Unique__c = '19410';
        zipCodeInstance4.name = '19410';
        zipCodeInstance4.geo_location__Latitude__s =  40.14000;
        zipCodeInstance4.geo_location__Longitude__s = -75.36000;
        insert zipCodeInstance4;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCodeList[1].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = zipCodeList[1].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject1);
        
        Campaign troopOrGroupVolunteerProject2 = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCodeList[2].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject2.Display_on_Website__c = true;
        troopOrGroupVolunteerProject2.Zip_Code__c = zipCodeList[2].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject2);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign troopGroupVolunteerJob3 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob3.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob3.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob3);
        
        Campaign troopGroupVolunteerJob4 = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob4.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob4.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob4);
        insert volunteerJobsCampaignList;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, volunteerJobsCampaignList[0].Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
        
        Test.startTest();
       // system.runAs(portalUser){
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerRenewal_TroopGroupRoleSearch;//new Pagereference('/apex/VolunteerRenewal_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        TroopGroupRoleSearchPage.getParameters().put('CampaignMemberIds',campaignMember.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
        
        VolunteerRenewal_RoleSearchController volunteerTroopGroupRoleSearch = new VolunteerRenewal_RoleSearchController();
        volunteerTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;
        
        volunteerTroopGroupRoleSearch.selectedRadius = '15';
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked = true;
        volunteerTroopGroupRoleSearch.addCampaignMember();
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);
        system.assertNotEquals(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignDistance, volunteerTroopGroupRoleSearch.parentCampaignWrapperList[2].campaignDistance);
        Contact contactNew = VolunteerRegistrationUtilty.contactRecord(contact.Id);
        Campaign campaignNew = VolunteerRegistrationUtilty.campaignRecord(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId); 
        CampaignMember campaignMemberToget = VolunteerRegistrationUtilty.campaignMemberRecord(contactNew.Id ,campaignNew.Id);
        system.assert(campaignMemberToGet.Id != null);
        Test.stopTest();
       // }
    }
   
   static testmethod void test_100_SearchByNameWithTwoSameNamesExists() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        
        Test.startTest();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('Troop Leader1',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        troopOrGroupVolunteerProject.Name = 'Troop Leader1';
        insert troopOrGroupVolunteerProject;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Leader2', troopOrGroupVolunteerProject.Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Leader3', troopOrGroupVolunteerProject.Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign troopGroupVolunteerJob3 = rC_GSATests.initializeCampaign('Troop Leader4', troopOrGroupVolunteerProject.Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob3.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob3.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob3);
        insert volunteerJobsCampaignList;
        
        system.debug('***volunteerJobsCampaignList***'+volunteerJobsCampaignList);
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, volunteerJobsCampaignList[0].Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
        
       // system.runAs(portalUser) { 
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerRenewal_TroopGroupRoleSearch;
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        TroopGroupRoleSearchPage.getParameters().put('CampaignMemberIds',campaignMember.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        system.debug('***TroopGroupRoleSearchPage***'+TroopGroupRoleSearchPage);
        
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        update troopGroupVolunteerJob1;
        
        VolunteerRenewal_RoleSearchController volunteerTroopGroupRoleSearch = new VolunteerRenewal_RoleSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = troopGroupVolunteerJob1.Name;
        //volunteerTroopGroupRoleSearch.selectedRadius = '5';
        
        system.debug('***troopGroupVolunteerJob1.Name***'+troopGroupVolunteerJob1.Name);
        system.debug('***userinfo.getProfileId().Name***'+userinfo.getProfileId());
        
        system.debug('***troopGroupVolunteerJob1***'+troopGroupVolunteerJob1);
        
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        for(Integer i = 0; i < volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size(); i++){
            if(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[i].childCampaignName != 'Unsure')
                volunteerTroopGroupRoleSearch.parentCampaignWrapperList[i].isCampaignChecked = true;
        }
        
        volunteerTroopGroupRoleSearch.addCampaignMember();
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        system.assertEquals(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked, true);
        Contact contactNew = VolunteerRegistrationUtilty.contactRecord(contact.Id);
        Campaign campaignNew = VolunteerRegistrationUtilty.campaignRecord(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId);
        CampaignMember campaignMemberToget = VolunteerRegistrationUtilty.campaignMemberRecord(contactNew.Id ,campaignNew.Id);
        system.assert(campaignMemberToGet.Id != null); 
        system.debug('***contactNewInSet***'+contactNew.Id);
        system.debug('***campaignNewInSet***'+campaignNew.Id);
        Test.stopTest();
     //  }
    }
    
    static testMethod void test_100_SearchForTroopRoleByNameAndZipCode() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        List<Zip_Code__c> zipCodeList = new List<Zip_Code__c>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.MailingPostalCode = '19312';
        insert contact;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '19312';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        zipCodeList.add(zipCodeInstance1);
        
        Zip_Code__c zipCodeInstance2 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance2.Zip_Code_Unique__c = '19373';
        zipCodeInstance2.name = '19373';
        zipCodeInstance2.geo_location__Latitude__s =  39.89000;
        zipCodeInstance2.geo_location__Longitude__s = -75.53000;
        zipCodeList.add(zipCodeInstance2);
        
        Zip_Code__c zipCodeInstance3 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance3.Zip_Code_Unique__c = '19409';
        zipCodeInstance3.name = '19409';
        zipCodeInstance3.geo_location__Latitude__s =  40.14000;
        zipCodeInstance3.geo_location__Longitude__s = -75.36000;
        zipCodeList.add(zipCodeInstance3);
        insert zipCodeList;
        
        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCodeList[1].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = zipCodeList[1].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject1);
        
        Campaign troopOrGroupVolunteerProject2 = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCodeList[2].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject2.Display_on_Website__c = true;
        troopOrGroupVolunteerProject2.Zip_Code__c = zipCodeList[2].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject2);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign troopGroupVolunteerJob3 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob3.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob3.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob3);
        
        Campaign troopGroupVolunteerJob4 = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob4.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob4.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob4);
        insert volunteerJobsCampaignList;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, volunteerJobsCampaignList[0].Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
        
        Test.startTest();      
     //   system.runAs(portalUser) {                     
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerRenewal_TroopGroupRoleSearch;//new Pagereference('/apex/VolunteerRenewal_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
         TroopGroupRoleSearchPage.getParameters().put('CampaignMemberIds',campaignMember.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);      
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
        
        VolunteerRenewal_RoleSearchController volunteerTroopGroupRoleSearch = new VolunteerRenewal_RoleSearchController();
        volunteerTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;          
        
        volunteerTroopGroupRoleSearch.selectedRadius = '15';   
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';                       
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();                 

        volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked = true;
        volunteerTroopGroupRoleSearch.skipNewRoles();
        volunteerTroopGroupRoleSearch.addCampaignMember();
            
         
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);
        system.assertNotEquals(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignDistance, volunteerTroopGroupRoleSearch.parentCampaignWrapperList[2].campaignDistance);
        Contact contactNew = VolunteerRegistrationUtilty.contactRecord(contact.Id);
        Campaign campaignNew = VolunteerRegistrationUtilty.campaignRecord(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId);
        CampaignMember campaignMemberToget = VolunteerRegistrationUtilty.campaignMemberRecord(contactNew.Id ,campaignNew.Id);
        system.assert(campaignMemberToGet.Id != null); 
        Test.stopTest();
      // }
    }
    
    static testMethod void test_100_UnsureOfVolunteerRole() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        
        
        Campaign[] campaignList = [Select Id from Campaign where Id = :troopOrGroupVolunteerProject.Id];
        Id parentCampaignId =  (campaignList != null && campaignList.size() > 0) ? campaignList[0].Id : '';
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader2', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, volunteerJobsCampaignList[0].Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
        
        Test.startTest();
       // system.runAs(portalUser) {
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerRenewal_TroopGroupRoleSearch;//new Pagereference('/apex/VolunteerRenewal_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        TroopGroupRoleSearchPage.getParameters().put('CampaignMemberIds',campaignMember.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
        
        
        List<Campaign> campaignDisplayonWebsiteProjectList = new List<Campaign>();
        
        for(Campaign campaign : volunteerProjectCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteProjectList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteProjectList.isEmpty())
            update campaignDisplayonWebsiteProjectList;
            
        VolunteerRenewal_RoleSearchController volunteerTroopGroupRoleSearch = new VolunteerRenewal_RoleSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Unsure';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';
        volunteerTroopGroupRoleSearch.selectedPageSize = '2';
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        for(Integer i = 0; i < volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size(); i++){
            if(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[i].childCampaignName == 'Unsure')
                volunteerTroopGroupRoleSearch.parentCampaignWrapperList[i].isCampaignChecked = true;
        }
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        system.assertEquals(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked, true);
        volunteerTroopGroupRoleSearch.whyAreYouUnsure = 'Not Sure';
        volunteerTroopGroupRoleSearch.createCampaignMemberOnUnsureCheck();
        Contact contactNew = VolunteerRegistrationUtilty.contactRecord(contact.Id);
        Campaign campaignNew = VolunteerRegistrationUtilty.campaignRecord(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId);
        CampaignMember campaignMemberToget = VolunteerRegistrationUtilty.campaignMemberRecord(contactNew.Id ,campaignNew.Id);
        system.assert(campaignMemberToGet.Id != null);
        Test.stopTest();
       // }
    }
  
    static testMethod void test_100_ClearSearch() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        system.debug('User ######'+Userinfo.getUserName());
        system.debug('Userinfo.getUserId()###'+Userinfo.getUserId());
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        Campaign[] campaignList = [Select Id from Campaign where Id = :troopOrGroupVolunteerProject.Id];
        Id parentCampaignId =  (campaignList != null && campaignList.size() > 0) ? campaignList[0].Id : '';
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', parentCampaignId, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, volunteerJobsCampaignList[0].Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
        
        Test.startTest();
       // system.runAs(portalUser) {
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerRenewal_TroopGroupRoleSearch;//new Pagereference('/apex/VolunteerRenewal_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        TroopGroupRoleSearchPage.getParameters().put('CampaignMemberIds',campaignMember.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        VolunteerRenewal_RoleSearchController volunteerTroopGroupRoleSearch = new VolunteerRenewal_RoleSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.zipCode = '11111';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';
        
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        volunteerTroopGroupRoleSearch.selectedPageSize = '3';
        volunteerTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        volunteerTroopGroupRoleSearch.nextButtonClick();
        volunteerTroopGroupRoleSearch.previousButtonClick();
        volunteerTroopGroupRoleSearch.clearSelections();
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        system.assertNotEquals(volunteerTroopGroupRoleSearch.zipCode, '11111');
        system.assertNotEquals(volunteerTroopGroupRoleSearch.troopOrGroupName, 'Troop Ledader1');
        
        Test.stopTest();
      //  }
    }
    
    static testMethod void test_100_SeeTroopNamesSameAsMySpelling() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, volunteerJobsCampaignList[0].Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
               
        Test.startTest();
        //system.runAs(portalUser) {
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerRenewal_TroopGroupRoleSearch;//new Pagereference('/apex/VolunteerRenewal_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        TroopGroupRoleSearchPage.getParameters().put('CampaignMemberIds',campaignMember.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        VolunteerRenewal_RoleSearchController volunteerTroopGroupRoleSearch = new VolunteerRenewal_RoleSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.zipCode = '11111';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';
        
        List<String> jasonString = VolunteerRenewal_RoleSearchController.searchCampaingNames('Troop Le');
        system.assertNotEquals(jasonString.size(), 0);
        Test.stopTest();
        //}
    }
     
    static testMethod void test_100_TroopNotFoundSearchingByName() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
         User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, volunteerJobsCampaignList[0].Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
        
        Test.startTest();
       // system.runAs(portalUser) {
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerRenewal_TroopGroupRoleSearch;//new Pagereference('/apex/VolunteerRenewal_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        TroopGroupRoleSearchPage.getParameters().put('CampaignMemberIds',campaignMember.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        try{
            VolunteerRenewal_RoleSearchController volunteerTroopGroupRoleSearch = new VolunteerRenewal_RoleSearchController();
            volunteerTroopGroupRoleSearch.troopOrGroupName = 'Alex';
            volunteerTroopGroupRoleSearch.selectedRadius = '5';
            volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
            
            System.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        }
        catch(Exception e){
        }
        Test.stopTest();
      //  }
    }
  
    static testMethod void test_100_TroopNotFoundSearchingbyRadius() {       
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        insert volunteerJobsCampaignList;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, volunteerJobsCampaignList[0].Id, false);
        campaignMember.Active__c = true;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;
        
        Test.startTest();
       // system.runAs(portalUser) {
        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        try{
            Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
            volunteerTroopGroupRoleSearch.zipCode = '12345';
            volunteerTroopGroupRoleSearch.selectedRadius = '5';
            volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
            system.debug('************'+volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size());
            System.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        }
        catch(Exception e){
        }
        Test.stopTest();
        //}
    }

    
  
    @isTest(seeAllData = true)
    static void test_101_Scenario1() {

        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Account insertedCouncilAccount = new Account();
        if(accountList != null && accountList.size() > 0) {
            for(Account insertedCouncil : accountList) {
                if(insertedCouncil.Id != null && insertedCouncil.RecordTypeId == rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE))
                    insertedCouncilAccount = insertedCouncil;
            }
        }

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(insertedCouncilAccount.Id, true);

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Birthdate = system.today().addYears(-15);
        insert contact;

        String lastCampaignYear = string.valueOf(date.today().addYears(1).year());
        Campaign membershipCampaign = rC_GSATests.initializeCampaign(lastCampaignYear + ' Membership', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        membershipCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.STANDARD_CAMPAIGN_RECORDTYPE);
        membershipCampaign.Program_Grade_Level__c = '1-Daisy';
        insert membershipCampaign;

        Background_Check__c BC = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Complete_Date__c = system.today().addYears(5),
            Background_Check_Status__c = 'Eligible'
        );
        insert BC;

        Opportunity oldMembershipOpportunity = rC_GSATests.initializeOpportunity(householdAccount, membershipCampaign, false);
        oldMembershipOpportunity.Background_Check__c = BC.Id;
        oldMembershipOpportunity.Contact__c = contact.Id;
        insert oldMembershipOpportunity;

        List<Pricebook2> pricebookList = [Select Name, IsActive, IsStandard, Id From Pricebook2 where Name = 'Girl Scouts USA' or IsStandard = true];
        Pricebook2 pricebookIsStandard;
        Pricebook2 pricebookGSA;

        for(Pricebook2 pricebook : pricebookList){
            if(pricebook.IsStandard)
                pricebookIsStandard = pricebook.clone(true, true, false, false);
            
            if(pricebook.Name.equalsIgnoreCase('Girl Scouts USA'))
                pricebookGSA = pricebook.clone(true, true, false, false);
        }

        Product2 newProduct = new Product2(
            IsActive = true, 
            Name = 'Adult Membership (Valid ' + date.today().month() + '/' + date.today().day() + '/' + date.today().year() + ' - ' +  
            date.today().addYears(1).month() + '/' + date.today().addYears(1).day() + '/' + date.today().addYears(1).year() + ')',  

            rC_Giving__Start_Date__c = date.today().addDays(5), 
            rC_Giving__End_Date__c = Date.today().addYears(1)
        );
        newProduct.RecordTypeId = rC_GSATests.getProductRecordTypeId(rC_GSATests.MEMBERSHIP_RECORDTYPE);
        insert newProduct;

        List<PricebookEntry> pricebookEntryList1 = new List<PricebookEntry>();

        PricebookEntry pricebookEntryIsStandard = new PricebookEntry(Product2Id = newProduct.Id, Pricebook2Id = pricebookIsStandard.Id, IsActive = true, UnitPrice = 1.0);
        pricebookEntryList1.add(pricebookEntryIsStandard);
        //PricebookEntry lifetimePricebookEntry = new PricebookEntry(Product2Id = newProduct.Id, Pricebook2Id = pricebookGSA.Id, IsActive = true, UnitPrice = 1.0);
        //pricebookEntryList1.add(lifetimePricebookEntry);
        insert pricebookEntryList1;
        
        OpportunityLineItem oldOpportunityLineItem = new OpportunityLineItem(
            PricebookEntryId = pricebookEntryList1[0].Id, 
            OpportunityId = oldMembershipOpportunity.Id, 
            Quantity = 10, TotalPrice = 100);
        insert oldOpportunityLineItem;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        parentCampaign.Display_on_Website__c = true;
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign.Background_Check_Needed__c = true;
        campaign.Display_on_Website__c = true;
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        campaignMember.Membership__c = oldMembershipOpportunity.Id;
        campaignMember.Active__c = true;
        campaignMember.Special_Handling__c = true;
        campaignMember.Continue_This_Position__c = 'Yes';
        insert campaignMember;

        String strCampaigns = string.valueOf(campaignMember.Id);

        test.startTest();
            Pagereference joinMemberShipPage = Page.VolunteerRenewal_MembershipInformation;
            joinMemberShipPage.getParameters().put('ContactId', contact.Id);
            joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
            joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
            Test.setCurrentPage(joinMemberShipPage);

            system.debug('url===> '+ joinMemberShipPage);

            VolunteerRenewal_MemberInfoController volunteerRenewalJoinMembershipInfoController = new VolunteerRenewal_MemberInfoController();
            Date myDate = date.newinstance(1960, 2, 17);

            volunteerRenewalJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/1990');
            volunteerRenewalJoinMembershipInfoController.gender = 'male';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'true';
            volunteerRenewalJoinMembershipInfoController.booleanTermsAndConditions = true;
            volunteerRenewalJoinMembershipInfoController.booleanContactPhotoOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactEmailOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanGrantRequested =false;
            volunteerRenewalJoinMembershipInfoController.mobilePhone = '2425435';
            volunteerRenewalJoinMembershipInfoController.preferredPhone = 'home';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'home';

            volunteerRenewalJoinMembershipInfoController.streetLine1 = 'streetLine1';
            volunteerRenewalJoinMembershipInfoController.streetLine2 = 'streetLine2';
            volunteerRenewalJoinMembershipInfoController.city = 'Pune';
            volunteerRenewalJoinMembershipInfoController.zipCode = '12345';
            volunteerRenewalJoinMembershipInfoController.country = 'USA';
            volunteerRenewalJoinMembershipInfoController.state = 'CA';
            volunteerRenewalJoinMembershipInfoController.isLifetime = false;

            volunteerRenewalJoinMembershipInfoController.booleanOppMembershipOnPaper = false;

            volunteerRenewalJoinMembershipInfoController.getGenders();
            volunteerRenewalJoinMembershipInfoController.getPreferredEmails();
            volunteerRenewalJoinMembershipInfoController.getPreferredPhones();
            volunteerRenewalJoinMembershipInfoController.getmembershipProductList();

            List<PricebookEntry > PricebookEntryList = VolunteerRegistrationUtilty.getPricebookEntryList();

            system.debug('PricebookEntryList===> '+ PricebookEntryList);
            PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;      
            volunteerRenewalJoinMembershipInfoController.membershipProduct = newpricebook.Id;      

            Pagereference pageReference = volunteerRenewalJoinMembershipInfoController.submit();

            Contact contactNew = [Select Id, Name, FirstName, LastName, Email, rC_Bios__Gender__c from Contact where Id = :contact.Id];
            CampaignMember updatedCampaignMember = [Select Id, Membership__c, ContactId, CampaignId From CampaignMember where Id =: campaignMember.Id];
            Opportunity[] createdOpportunityList = [select Id, Name, Renewal__c, Background_Check__c from Opportunity where Id = : updatedCampaignMember.Membership__c limit 1];

            OpportunityLineItem opportunityLineItem = [
                Select PricebookEntry.Pricebook2Id, PricebookEntry.Name, PricebookEntryId, OpportunityId 
                From OpportunityLineItem 
                where OpportunityId =: createdOpportunityList[0].Id
            ];

            PricebookEntry pricebookEntry1 = [
                Select Id, Name, IsActive, Product2.Description, Product2Id, Pricebook2Id, Product2.rC_Giving__Start_Date__c, Product2.rC_Giving__End_Date__c
                  From PricebookEntry 
                 where Id = : opportunityLineItem.PricebookEntryId
                 limit 1
            ];

            Pricebook2 GSPricebook = [Select Name, Id, Description From Pricebook2 where Name = 'Girl Scouts USA' and IsActive = true limit 1];

            /*if(pageReference.getUrl() !=null && pageReference.getUrl().contains('?')){
                String pagereferenceURL = pageReference.getUrl();
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEERRENEWAL_PAYMENT');
            }*/
            //system.assert(pageReference.getUrl().contains('VolunteerRenewal_Payment'));

            //Asserts that membership opportunity is created
            /*
            system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), null);
            system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), '');
            system.assertEquals(pageReference.getParameters().get('OpportunityId'), updatedCampaignMember.Membership__c);

            //Asserts the pricebook is 'Girl Scouts USA' pricebook is used to create a Membership Opportunity
            system.assertEquals(GSPricebook.Id, opportunityLineItem.PricebookEntry.Pricebook2Id);
            
            //system.assertEquals(pricebookEntry1.Product2.Description, newProduct.Description);

            system.assertEquals(volunteerRenewalJoinMembershipInfoController.firstName, contactNew.FirstName);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.lastName, contactNew.LastName);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.email, contactNew.Email);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.gender.toUpperCase(), contactNew.rC_Bios__Gender__c.toUpperCase());

            if(createdOpportunityList.size() > 0) {
                system.assertEquals(createdOpportunityList[0].Background_Check__c, oldMembershipOpportunity.Background_Check__c);
                system.assertEquals(createdOpportunityList[0].Renewal__c, true);
            }
            */
        test.stopTest();
    }

    @isTest(seeAllData = true)
    static void test_101_Scenario2() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true); 

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Birthdate = system.today().addYears(-15);
        contact.HomePhone = '9999999999';
        insert contact;

        String lastCampaignYear = string.valueOf(date.today().addYears(1).year());
        Campaign membershipCampaign = rC_GSATests.initializeCampaign(lastCampaignYear + ' Membership', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        membershipCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.STANDARD_CAMPAIGN_RECORDTYPE);
        membershipCampaign.Program_Grade_Level__c = '1-Daisy';
        insert membershipCampaign;

        List<Pricebook2> pricebookList = [Select Name, IsActive, IsStandard, Id From Pricebook2 where Name = 'Girl Scouts USA' or IsStandard = true];
        Pricebook2 pricebookIsStandard;
        Pricebook2 pricebookGSA;

        for(Pricebook2 pricebook : pricebookList){
            if(pricebook.IsStandard)
                pricebookIsStandard = pricebook.clone(true, true, false, false);
            
            if(pricebook.Name.equalsIgnoreCase('Girl Scouts USA'))
                pricebookGSA = pricebook.clone(true, true, false, false);
        }

        Product2 newProduct = new Product2(
            IsActive = true, 
            Name = 'Adult Membership (Valid ' + date.today().month() + '/' + date.today().day() + '/' + date.today().year() + ' - ' +  
            date.today().addYears(1).month() + '/' + date.today().addYears(1).day() + '/' + date.today().addYears(1).year() + ')',  

            rC_Giving__Start_Date__c = date.today().addDays(5), 
            rC_Giving__End_Date__c = Date.today().addYears(1)
        );
        newProduct.RecordTypeId = rC_GSATests.getProductRecordTypeId(rC_GSATests.MEMBERSHIP_RECORDTYPE);
        insert newProduct;

        List<PricebookEntry> pricebookEntryList1 = new List<PricebookEntry>();

        PricebookEntry pricebookEntryIsStandard = new PricebookEntry(Product2Id = newProduct.Id, Pricebook2Id = pricebookIsStandard.Id, IsActive = true, UnitPrice = 1.0);
        pricebookEntryList1.add(pricebookEntryIsStandard);
        PricebookEntry lifetimePricebookEntry = new PricebookEntry(Product2Id = newProduct.Id, Pricebook2Id = pricebookGSA.Id, IsActive = true, UnitPrice = 1.0);
        pricebookEntryList1.add(lifetimePricebookEntry);
        insert pricebookEntryList1;

        Background_Check__c BC = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Status__c = 'Eligible'
        );
        insert BC;

        Opportunity oldMembershipOpportunity = rC_GSATests.initializeOpportunity(householdAccount, membershipCampaign, false);
        oldMembershipOpportunity.Background_Check__c = BC.Id;
        oldMembershipOpportunity.Contact__c = contact.Id;
        insert oldMembershipOpportunity;

        OpportunityLineItem oldOpportunityLineItem = new OpportunityLineItem(
            PricebookEntryId = pricebookEntryList1[1].Id, 
            OpportunityId = oldMembershipOpportunity.Id, 
            Quantity = 10, TotalPrice = 100);
        insert oldOpportunityLineItem;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        campaignMember.Membership__c = oldMembershipOpportunity.Id;
        campaignMember.Active__c = true;
        insert campaignMember;

        String strCampaigns = string.valueOf(campaignMember.Id);

        test.startTest();
            Pagereference joinMemberShipPage = Page.VolunteerRenewal_MembershipInformation;//new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
            joinMemberShipPage.getParameters().put('ContactId', contact.Id);
            joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
            joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
            Test.setCurrentPage(joinMemberShipPage);

            system.debug('2.url===> '+ joinMemberShipPage);

            VolunteerRenewal_MemberInfoController volunteerRenewalJoinMembershipInfoController = new VolunteerRenewal_MemberInfoController();
            Date myDate = date.newinstance(1960, 2, 17);

            volunteerRenewalJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/2011');
            volunteerRenewalJoinMembershipInfoController.gender = 'male';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'true';
            volunteerRenewalJoinMembershipInfoController.booleanTermsAndConditions = true;
            volunteerRenewalJoinMembershipInfoController.booleanContactPhotoOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactEmailOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanGrantRequested =false;
            volunteerRenewalJoinMembershipInfoController.mobilePhone = '2425435';
            volunteerRenewalJoinMembershipInfoController.preferredPhone = 'home';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'home';

            volunteerRenewalJoinMembershipInfoController.streetLine1 = 'streetLine1';
            volunteerRenewalJoinMembershipInfoController.streetLine2 = 'streetLine2';
            volunteerRenewalJoinMembershipInfoController.city = 'Pune';
            volunteerRenewalJoinMembershipInfoController.zipCode = '12345';
            volunteerRenewalJoinMembershipInfoController.country = 'USA';
            volunteerRenewalJoinMembershipInfoController.state = 'CA';
            volunteerRenewalJoinMembershipInfoController.isLifetime = false;

            volunteerRenewalJoinMembershipInfoController.booleanOppMembershipOnPaper = false;

            volunteerRenewalJoinMembershipInfoController.getGenders();
            volunteerRenewalJoinMembershipInfoController.getPreferredEmails();
            volunteerRenewalJoinMembershipInfoController.getPreferredPhones();
            volunteerRenewalJoinMembershipInfoController.getmembershipProductList();

            List<PricebookEntry > PricebookEntryList = VolunteerRegistrationUtilty.getPricebookEntryList();

            system.debug('PricebookEntryList===> '+ PricebookEntryList);
            PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;      
            volunteerRenewalJoinMembershipInfoController.membershipProduct = newpricebook.Id;      

            Pagereference pageReference = volunteerRenewalJoinMembershipInfoController.submit();

            Contact contactNew = [Select Id, Name, FirstName, LastName, Email, rC_Bios__Gender__c from Contact where Id = :contact.Id];
            CampaignMember updatedCampaignMember = [Select Id, Membership__c, ContactId, CampaignId From CampaignMember where Id =: campaignMember.Id];
            Opportunity[] createdOpportunityList = [select Id, Name, Renewal__c, Background_Check__c from Opportunity where Id = : updatedCampaignMember.Membership__c limit 1];

            OpportunityLineItem opportunityLineItem = [
                Select PricebookEntry.Pricebook2Id, PricebookEntry.Name, PricebookEntryId, OpportunityId 
                From OpportunityLineItem 
                where OpportunityId =: createdOpportunityList[0].Id
            ];

            PricebookEntry pricebookEntry1 = [
                Select Id, Name, IsActive, Product2.Description, Product2Id, Pricebook2Id, Product2.rC_Giving__Start_Date__c, Product2.rC_Giving__End_Date__c
                  From PricebookEntry 
                 where Id = : opportunityLineItem.PricebookEntryId
                 limit 1
            ];

            Pricebook2 GSPricebook = [Select Name, Id, Description From Pricebook2 where Name = 'Girl Scouts USA' and IsActive = true limit 1];

            /*if(pageReference.getUrl() !=null && pageReference.getUrl().contains('?')){
                String pagereferenceURL = pageReference.getUrl();
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEERRENEWAL_PAYMENT');
            }*/
            //system.assert(pageReference.getUrl().contains('VolunteerRenewal_Payment'));

            //Asserts that membership opportunity is created
/*
            system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), null);
            system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), '');
            system.assertEquals(pageReference.getParameters().get('OpportunityId'), updatedCampaignMember.Membership__c);

            //Asserts the pricebook is 'Girl Scouts USA' pricebook is used to create a Membership Opportunity
            system.assertEquals(GSPricebook.Id, opportunityLineItem.PricebookEntry.Pricebook2Id);
            //system.assertEquals(pricebookEntry1.Product2.Description, newProduct.Description);

            system.assertEquals(volunteerRenewalJoinMembershipInfoController.firstName, contactNew.FirstName);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.lastName, contactNew.LastName);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.email, contactNew.Email);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.gender.toUpperCase(), contactNew.rC_Bios__Gender__c.toUpperCase());

            if(createdOpportunityList.size() > 0) {
                system.assertEquals(createdOpportunityList[0].Background_Check__c, oldMembershipOpportunity.Background_Check__c);
                system.assertEquals(createdOpportunityList[0].Renewal__c, true);
            }
     */       
        test.stopTest();
    }
    
    @isTest(seeAllData = true)
    static void test_101_Scenario3() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true); 

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Birthdate = system.today().addYears(-15);
        contact.HomePhone = '9999999999';
        insert contact;

        String lastCampaignYear = string.valueOf(system.today().year() - 1);
        Campaign membershipCampaign = rC_GSATests.initializeCampaign(lastCampaignYear + ' Membership', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        membershipCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.STANDARD_CAMPAIGN_RECORDTYPE);
        membershipCampaign.Program_Grade_Level__c = '1-Daisy';
        insert membershipCampaign;
        
        Background_Check__c BC = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Status__c = 'Eligible'
        );
        insert BC;

        Opportunity oldMembershipOpportunity = rC_GSATests.initializeOpportunity(householdAccount, membershipCampaign, false);
        oldMembershipOpportunity.Contact__c = contact.Id;
        oldMembershipOpportunity.Background_Check__c = BC.Id;
        insert oldMembershipOpportunity;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        campaignMember.Membership__c = oldMembershipOpportunity.Id;
        campaignMember.Active__c = true;
        insert campaignMember;

        String strCampaigns = string.valueOf(campaignMember.Id);

        test.startTest();
            Pagereference joinMemberShipPage = Page.VolunteerRenewal_MembershipInformation;//new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
            joinMemberShipPage.getParameters().put('ContactId', contact.Id);
            joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
            joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
            Test.setCurrentPage(joinMemberShipPage);

            system.debug('2.url===> '+ joinMemberShipPage);

            VolunteerRenewal_MemberInfoController volunteerRenewalJoinMembershipInfoController = new VolunteerRenewal_MemberInfoController();
            Date myDate = date.newinstance(1960, 2, 17);

            volunteerRenewalJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/2011');
            volunteerRenewalJoinMembershipInfoController.gender = 'male';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'true';
            volunteerRenewalJoinMembershipInfoController.booleanTermsAndConditions = true;
            volunteerRenewalJoinMembershipInfoController.booleanContactPhotoOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactEmailOptIn = false;
            volunteerRenewalJoinMembershipInfoController.mobilePhone = '2425435';
            volunteerRenewalJoinMembershipInfoController.preferredPhone = 'home';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'home';

            volunteerRenewalJoinMembershipInfoController.streetLine1 = 'streetLine1';
            volunteerRenewalJoinMembershipInfoController.streetLine2 = 'streetLine2';
            volunteerRenewalJoinMembershipInfoController.city = 'Pune';
            volunteerRenewalJoinMembershipInfoController.zipCode = '12345';
            volunteerRenewalJoinMembershipInfoController.country = 'USA';
            volunteerRenewalJoinMembershipInfoController.state = 'CA';
            volunteerRenewalJoinMembershipInfoController.isLifetime = false;

            volunteerRenewalJoinMembershipInfoController.booleanOppMembershipOnPaper = true;
            volunteerRenewalJoinMembershipInfoController.booleanGrantRequested =false;

            volunteerRenewalJoinMembershipInfoController.getGenders();
            volunteerRenewalJoinMembershipInfoController.getPreferredEmails();
            volunteerRenewalJoinMembershipInfoController.getPreferredPhones();
            volunteerRenewalJoinMembershipInfoController.getmembershipProductList();

            List<PricebookEntry > PricebookEntryList = VolunteerRegistrationUtilty.getPricebookEntryList();

            system.debug('PricebookEntryList===> '+ PricebookEntryList);
            PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;      
            volunteerRenewalJoinMembershipInfoController.membershipProduct = newpricebook.Id;      

            Pagereference pageReference = volunteerRenewalJoinMembershipInfoController.submit();
/*
            Contact contactNew = [Select Id, Name, FirstName, LastName, Email, rC_Bios__Gender__c from Contact where Id = :contact.Id];
            CampaignMember updatedCampaignMember = [Select Id, Membership__c, ContactId, CampaignId From CampaignMember where Id =: campaignMember.Id];
            Opportunity[] createdOpportunityList = [select Id, Name, Renewal__c, Background_Check__c from Opportunity where Id = : updatedCampaignMember.Membership__c limit 1];

            OpportunityLineItem opportunityLineItem = [
                Select PricebookEntry.Pricebook2Id, PricebookEntry.Name, PricebookEntryId, OpportunityId 
                From OpportunityLineItem 
                where OpportunityId =: createdOpportunityList[0].Id
            ];

            PricebookEntry pricebookEntry1 = [
                Select Id, Name, IsActive, Product2.Description, Product2Id, Pricebook2Id, Product2.rC_Giving__Start_Date__c, Product2.rC_Giving__End_Date__c
                  From PricebookEntry 
                 where Id = : opportunityLineItem.PricebookEntryId
                 limit 1
            ];

            Pricebook2 GSPricebook = [Select Name, Id, Description From Pricebook2 where Name = 'Girl Scouts USA' and IsActive = true limit 1];

            if(pageReference.getUrl() !=null && pageReference.getUrl().contains('?')){
                String pagereferenceURL = pageReference.getUrl();
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEERRENEWAL_MEMBERINFOTHANKYOU');
            }
            //system.assert(pageReference.getUrl().contains('VolunteerRenewal_MemberInfoThankYou'));
            system.assertEquals(pageReference.getParameters().get('CashOrCheck'), 'true');

            //Asserts that membership opportunity is created
            system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), null);
            system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), '');
            system.assertEquals(pageReference.getParameters().get('OpportunityId'), updatedCampaignMember.Membership__c);

            //Asserts the pricebook is 'Girl Scouts USA' pricebook is used to create a Membership Opportunity
            system.assertEquals(GSPricebook.Id, opportunityLineItem.PricebookEntry.Pricebook2Id);
            //system.assertEquals(pricebookEntry1.Product2.Description, 'Adult Membership (Valid 10/1/2015 - 9/30/2016)');

            system.assertEquals(volunteerRenewalJoinMembershipInfoController.firstName, contactNew.FirstName);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.lastName, contactNew.LastName);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.email, contactNew.Email);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.gender.toUpperCase(), contactNew.rC_Bios__Gender__c.toUpperCase());

            if(createdOpportunityList.size() > 0) {
                system.assertEquals(createdOpportunityList[0].Background_Check__c, oldMembershipOpportunity.Background_Check__c);
                system.assertEquals(createdOpportunityList[0].Renewal__c, true);
            }
            */
        test.stopTest();
    }
   @isTest(seeAllData = true)
    static void test_101_Scenario4() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true); 

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Birthdate = system.today().addYears(-15);
        contact.HomePhone = '9999999999';
        insert contact;

        String lastCampaignYear = string.valueOf(system.today().year() - 1);
        Campaign membershipCampaign = rC_GSATests.initializeCampaign(lastCampaignYear + ' Membership', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        membershipCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.STANDARD_CAMPAIGN_RECORDTYPE);
        membershipCampaign.Program_Grade_Level__c = '1-Daisy';
        insert membershipCampaign;
        
        Background_Check__c BC = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Status__c = 'Eligible'
        );
        insert BC;

        Opportunity oldMembershipOpportunity = rC_GSATests.initializeOpportunity(householdAccount, membershipCampaign, false);
        oldMembershipOpportunity.Background_Check__c = BC.Id;
        oldMembershipOpportunity.Contact__c = contact.Id;
        insert oldMembershipOpportunity;

        String strCampaigns = '';

        test.startTest();
            Pagereference joinMemberShipPage = Page.VolunteerRenewal_MembershipInformation;//new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
            joinMemberShipPage.getParameters().put('ContactId', contact.Id);
            joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
            joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
            Test.setCurrentPage(joinMemberShipPage);

            system.debug('2.url===> '+ joinMemberShipPage);

            VolunteerRenewal_MemberInfoController volunteerRenewalJoinMembershipInfoController = new VolunteerRenewal_MemberInfoController();
            Date myDate = date.newinstance(1960, 2, 17);

            volunteerRenewalJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/2010');
            volunteerRenewalJoinMembershipInfoController.gender = 'male';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'true';
            volunteerRenewalJoinMembershipInfoController.booleanTermsAndConditions = true;
            volunteerRenewalJoinMembershipInfoController.booleanContactPhotoOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactEmailOptIn = false;
            volunteerRenewalJoinMembershipInfoController.mobilePhone = '2425435';
            volunteerRenewalJoinMembershipInfoController.preferredPhone = 'home';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'home';

            volunteerRenewalJoinMembershipInfoController.streetLine1 = 'streetLine1';
            volunteerRenewalJoinMembershipInfoController.streetLine2 = 'streetLine2';
            volunteerRenewalJoinMembershipInfoController.city = 'Pune';
            volunteerRenewalJoinMembershipInfoController.zipCode = '12345';
            volunteerRenewalJoinMembershipInfoController.country = 'USA';
            volunteerRenewalJoinMembershipInfoController.state = 'CA';
            volunteerRenewalJoinMembershipInfoController.isLifetime = false;

            volunteerRenewalJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
            volunteerRenewalJoinMembershipInfoController.booleanGrantRequested =false;

            volunteerRenewalJoinMembershipInfoController.getGenders();
            volunteerRenewalJoinMembershipInfoController.getPreferredEmails();
            volunteerRenewalJoinMembershipInfoController.getPreferredPhones();
            volunteerRenewalJoinMembershipInfoController.getmembershipProductList();

            List<PricebookEntry > PricebookEntryList = VolunteerRegistrationUtilty.getPricebookEntryList();

            PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;      
            volunteerRenewalJoinMembershipInfoController.membershipProduct = newpricebook.Id;      

            Pagereference pageReference = volunteerRenewalJoinMembershipInfoController.submit();
            /*
            Pricebook2 GSPricebook = [Select Name, Id, Description From Pricebook2 where Name = 'Girl Scouts USA' and IsActive = true limit 1];

            String newOpportunityId = pageReference.getParameters().get('OpportunityId');
            
            Contact contactNew = [Select Id, Name, FirstName, LastName, Email, rC_Bios__Gender__c from Contact where Id = :contact.Id];
            Opportunity createdNewOpportunity = [select Id, Name, Renewal__c, Background_Check__c from Opportunity where Id = : newOpportunityId limit 1];

            system.debug('=== createdNewOpportunity ====: ' + createdNewOpportunity);
            OpportunityLineItem opportunityLineItem = [
                Select PricebookEntry.Pricebook2Id, PricebookEntry.Name, PricebookEntryId, OpportunityId 
                From OpportunityLineItem 
                where OpportunityId =: createdNewOpportunity.Id
            ];

            PricebookEntry pricebookEntry1 = [
                Select Id, Name, IsActive, Product2.Description, Product2Id, Pricebook2Id, Product2.rC_Giving__Start_Date__c, Product2.rC_Giving__End_Date__c
                  From PricebookEntry 
                 where Id = : opportunityLineItem.PricebookEntryId
                 limit 1
            ];

            if(pageReference!= null && pageReference.getUrl() !=null && pageReference.getUrl().contains('?')){
                String pagereferenceURL = pageReference.getUrl();
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEERRENEWAL_PAYMENT');
            }

            system.assertEquals(GSPricebook.Id, opportunityLineItem.PricebookEntry.Pricebook2Id);
            //system.assertEquals(pricebookEntry1.Product2.Description, 'Adult Membership (Valid 10/1/2015 - 9/30/2016)');
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.firstName, contactNew.FirstName);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.lastName, contactNew.LastName);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.email, contactNew.Email);
            system.assertEquals(volunteerRenewalJoinMembershipInfoController.gender.toUpperCase(), contactNew.rC_Bios__Gender__c.toUpperCase());
            system.assertEquals(createdNewOpportunity.Renewal__c, true);
            */
        test.stopTest();
    }
    
        @isTest(seeAllData = true)
    static void test_101_Scenario5() {

        set<Id> campaignMemberIdSet = new set<Id>();
        List<Campaign> campaignList = new List<Campaign>();
        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Account insertedCouncilAccount = new Account();
        if(accountList != null && accountList.size() > 0) {
            for(Account insertedCouncil : accountList) {
                if(insertedCouncil.Id != null && insertedCouncil.RecordTypeId == rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE))
                    insertedCouncilAccount = insertedCouncil;
            }
        }
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(insertedCouncilAccount.Id, true);

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Birthdate = system.today().addYears(-15);
        contact.HomePhone = '9999999999';
        insert contact;

        Campaign membershipCampaign = rC_GSATests.initializeCampaign('Lifetime Membership', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        membershipCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.STANDARD_CAMPAIGN_RECORDTYPE);
        membershipCampaign.IsActive = true;
        membershipCampaign.Background_Check_Needed__c = true;
        membershipCampaign.Display_on_Website__c = true;
        membershipCampaign.Program_Grade_Level__c = '1-Daisy';
        upsert membershipCampaign;

        PricebookEntry lifetimePricebookEntry;
        map<Id, PricebookEntry> PricebookEntryMap = new map<Id, PricebookEntry>([
            Select Id
                 , Name
                 , UnitPrice
                 , Product2Id
                 , Pricebook2Id
                 , Pricebook2.Id
                 , Pricebook2.Name
                 , Pricebook2.IsActive
                 , Pricebook2.Description
                 , Product2.rC_Giving__Start_Date__c
                 , Product2.rC_Giving__End_Date__c
                 , Product2.Name
              From PricebookEntry
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
             //limit 1
        ]);
        system.debug('PricebookEntryMap============='+PricebookEntryMap.values());
        Integer Year = Date.Today().Year() + 1;
        String sYear = String.valueOf(Year);
        Integer currentYear = Date.Today().Year();
        String currentYearStr = String.valueOf(currentYear);

        for(PricebookEntry pricebookEntry : PricebookEntryMap.values()) {
            if ((pricebookEntry.Name.toUpperCase().contains('ADULT') || pricebookEntry.Name.toUpperCase().contains('LIFETIME')) && (!pricebookEntry.Name.contains(sYear)) && (!pricebookEntry.Name.contains(currentYearStr)))
                system.debug('******life*****');
                lifetimePricebookEntry = PricebookEntryMap.get(pricebookEntry.Id);
        }
        system.debug('******lifetimePricebookEntry******'+lifetimePricebookEntry);
        system.debug('******contact.Id******'+contact.Id);
        system.debug('******lifetimePricebookEntry.Product2.rC_Giving__Start_Date__c******'+lifetimePricebookEntry.Product2.rC_Giving__Start_Date__c);
        Background_Check__c BC = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Status__c = 'Eligible',
            Background_Check_Complete_Date__c = lifetimePricebookEntry.Product2.rC_Giving__Start_Date__c.addDays(-10)
        );
        insert BC;

        Opportunity oldMembershipOpportunity = rC_GSATests.initializeOpportunity(householdAccount, membershipCampaign, false);
        oldMembershipOpportunity.Background_Check__c = BC.Id;
        oldMembershipOpportunity.Type = 'Lifetime Membership';
        oldMembershipOpportunity.Contact__c = contact.Id;
        insert oldMembershipOpportunity;

        OpportunityLineItem oldOpportunityLineItem = new OpportunityLineItem(
            OpportunityId = oldMembershipOpportunity.Id,
            PricebookEntryId = lifetimePricebookEntry.Id,
            Quantity = 1,
            TotalPrice = 20
        );
        insert oldOpportunityLineItem;


        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign.Program_Grade_Level__c = '1-Daisy';
        campaign.Background_Check_Needed__c = true;
        campaignList.add(campaign);

        Campaign campaign2 = rC_GSATests.initializeCampaign('Join Membership Test Campaign2', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign2.Program_Grade_Level__c = '1-Daisy';
        campaign2.Background_Check_Needed__c = true;
        campaignList.add(campaign2);
        insert campaignList;

        List<CampaignMember> campaignMemberList = new List<CampaignMember>();

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        campaignMember.Membership__c = oldMembershipOpportunity.Id;
        campaignMember.Active__c = true;
        campaignMemberList.add(campaignMember);

        campaignMemberIdSet.add(campaignMember.Id);
        CampaignMember campaignMember1 = new CampaignMember(ContactId = contact.Id, CampaignId= campaign2.Id);
        campaignMember1.Active__c = true;
        campaignMemberList.add(campaignMember1);
        insert campaignMemberList;

        campaignMemberIdSet.add(campaignMember1.Id);
        String strCampaigns = string.valueOf(campaignMember.Id) + ',' + string.valueOf(campaignMember1.Id);

        test.startTest();

            Pagereference joinMemberShipPage = Page.VolunteerRenewal_MembershipInformation;//new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
            joinMemberShipPage.getParameters().put('ContactId', contact.Id);
            joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
            joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
            Test.setCurrentPage(joinMemberShipPage);

            VolunteerRenewal_MemberInfoController volunteerRenewalJoinMembershipInfoController = new VolunteerRenewal_MemberInfoController();
            Date myDate = date.newinstance(1960, 2, 17);

            volunteerRenewalJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/1990');
            volunteerRenewalJoinMembershipInfoController.gender = 'male';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'true';
            volunteerRenewalJoinMembershipInfoController.booleanTermsAndConditions = true;
            volunteerRenewalJoinMembershipInfoController.booleanContactPhotoOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactEmailOptIn = false;
            volunteerRenewalJoinMembershipInfoController.mobilePhone = '2425435';
            volunteerRenewalJoinMembershipInfoController.preferredPhone = 'home';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'home';

            volunteerRenewalJoinMembershipInfoController.streetLine1 = 'streetLine1';
            volunteerRenewalJoinMembershipInfoController.streetLine2 = 'streetLine2';
            volunteerRenewalJoinMembershipInfoController.city = 'Pune';
            volunteerRenewalJoinMembershipInfoController.zipCode = '12345';
            volunteerRenewalJoinMembershipInfoController.country = 'USA';
            volunteerRenewalJoinMembershipInfoController.state = 'CA';

            volunteerRenewalJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
            volunteerRenewalJoinMembershipInfoController.booleanGrantRequested =false;

            volunteerRenewalJoinMembershipInfoController.getGenders();
            volunteerRenewalJoinMembershipInfoController.getPreferredEmails();
            volunteerRenewalJoinMembershipInfoController.getPreferredPhones();
            volunteerRenewalJoinMembershipInfoController.getmembershipProductList();

            List<PricebookEntry > PricebookEntryList = VolunteerRegistrationUtilty.getPricebookEntryList();

            PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;      
            volunteerRenewalJoinMembershipInfoController.membershipProduct = newpricebook.Id;      

            Pagereference pageReference = volunteerRenewalJoinMembershipInfoController.submit();

            map<Id, CampaignMember> updatedCampaignMemberMap = new map<Id, CampaignMember>([
                Select Id, Membership__c, ContactId, CampaignId, Display_Renewal__c
                  From CampaignMember 
                 where Id IN :campaignMemberIdSet
            ]);

            BC.Background_Check_Status__c = 'Eligible';
            BC.Background_Check_Complete_Date__c = lifetimePricebookEntry.Product2.rC_Giving__Start_Date__c.addDays(-10);
            update BC;

            Opportunity createdOpportunity = [select Id, Name, Renewal__c, Background_Check__c, Membership_Status__c from Opportunity where Id = : oldMembershipOpportunity.Id limit 1];

            for(CampaignMember updatedCampaignMember : updatedCampaignMemberMap.values()) {
                system.debug('=== updatedCampaignMember Membership__c      =====:  ' + updatedCampaignMember.Membership__c);
                system.debug('=== updatedCampaignMember Display_Renewal__c =====:  ' + updatedCampaignMember.Display_Renewal__c);
                system.assertEquals(updatedCampaignMember.Membership__c, oldMembershipOpportunity.Id);
                system.assertEquals(updatedCampaignMember.Display_Renewal__c, false);
            }

            if(pageReference.getUrl() !=null && pageReference.getUrl().contains('?')){
                String pagereferenceURL = pageReference.getUrl();
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                //system.assertEquals(pageName,'VOLUNTEERRENEWAL_PAYMENT');
            }
            //system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), null);
            system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), '');
            system.assertEquals(pageReference.getParameters().get('OpportunityId'), null);

        test.stopTest();
    }

    @isTest(seeAllData = true)
    static void test_101_Scenario6() {

        set<Id> campaignMemberIdSet = new set<Id>();
        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Account insertedCouncilAccount = new Account();
        if(accountList != null && accountList.size() > 0) {
            for(Account insertedCouncil : accountList) {
                if(insertedCouncil.Id != null && insertedCouncil.RecordTypeId == rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE))
                    insertedCouncilAccount = insertedCouncil;
            }
        }

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(insertedCouncilAccount.Id, true);

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Birthdate = system.today().addYears(-15);
        contact.HomePhone = '9999999999';
        insert contact;

        Campaign membershipCampaign = rC_GSATests.initializeCampaign('Lifetime Membership', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        membershipCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.STANDARD_CAMPAIGN_RECORDTYPE);
        membershipCampaign.IsActive = true;
        membershipCampaign.Display_on_Website__c = true;
        membershipCampaign.Program_Grade_Level__c = '1-Daisy';
        upsert membershipCampaign;

        Background_Check__c BC = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Status__c = 'Eligible',
            Background_Check_Complete_Date__c = date.today().addDays(10)//lifetimePricebookEntry.Product2.rC_Giving__Start_Date__c.addDays(10)
        );
        insert BC;

        Opportunity oldMembershipOpportunity = rC_GSATests.initializeOpportunity(householdAccount, membershipCampaign, false);
        oldMembershipOpportunity.Background_Check__c = BC.Id;
        oldMembershipOpportunity.Type = 'Lifetime Membership';
        oldMembershipOpportunity.Contact__c = contact.Id;
        oldMembershipOpportunity.StageName = 'Open';
        insert oldMembershipOpportunity;

        List<Pricebook2> pricebookList = [Select Name, IsActive, IsStandard, Id From Pricebook2 where Name = 'Girl Scouts USA' or IsStandard = true];
        Pricebook2 pricebookIsStandard;
        Pricebook2 pricebookGSA;

        for(Pricebook2 pricebook : pricebookList){
            if(pricebook.IsStandard)
                pricebookIsStandard = pricebook.clone(true, true, false, false);
            
            if(pricebook.Name.equalsIgnoreCase('Girl Scouts USA'))
                pricebookGSA = pricebook.clone(true, true, false, false);
        }

        Product2 newProduct = new Product2(IsActive = true, Name = 'Lifetime Membership (Valid 10/1/2014 - 9/30/2050)',  rC_Giving__Start_Date__c = date.today().addDays(5), rC_Giving__End_Date__c = Date.today().addYears(20));
        newProduct.RecordTypeId = rC_GSATests.getProductRecordTypeId(rC_GSATests.MEMBERSHIP_RECORDTYPE);
        insert newProduct;

        List<PricebookEntry> pricebookEntryList1 = new List<PricebookEntry>();

        PricebookEntry pricebookEntryIsStandard = new PricebookEntry(Product2Id = newProduct.Id, Pricebook2Id = pricebookIsStandard.Id, IsActive = true, UnitPrice = 1.0);
        pricebookEntryList1.add(pricebookEntryIsStandard);
        PricebookEntry lifetimePricebookEntry = new PricebookEntry(Product2Id = newProduct.Id, Pricebook2Id = pricebookGSA.Id, IsActive = true, UnitPrice = 1.0);
        pricebookEntryList1.add(lifetimePricebookEntry);
        insert pricebookEntryList1;

        OpportunityLineItem oldOpportunityLineItem = new OpportunityLineItem(
            PricebookEntryId = pricebookEntryList1[1].Id, 
            OpportunityId = oldMembershipOpportunity.Id, 
            Quantity = 10, TotalPrice = 100);
        insert oldOpportunityLineItem;

        BC.Background_Check_Status__c = 'Expired';
        BC.Background_Check_Complete_Date__c = Date.today().addDays(-10);
        update BC;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        insert parentCampaign;

        List<Campaign> campaignList = new List<Campaign>();
        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign.Program_Grade_Level__c = '1-Daisy';
        campaign.Background_Check_Needed__c = true;
        campaignList.add(campaign);

        Campaign campaign2 = rC_GSATests.initializeCampaign('Join Membership Test Campaign2', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign2.Program_Grade_Level__c = '1-Daisy';
        campaign2.Background_Check_Needed__c = true;
        campaignList.add(campaign2);
        insert campaignList;

        List<CampaignMember> campaignMemberList = new List<CampaignMember>();

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        campaignMember.Membership__c = oldMembershipOpportunity.Id;
        campaignMember.Active__c = true;
        campaignMember.Continue_This_Position__c = 'Yes';
        campaignMemberList.add(campaignMember);

        CampaignMember campaignMember1 = new CampaignMember(ContactId = contact.Id, CampaignId= campaign2.Id);
        campaignMember1.Active__c = true;
        campaignMember1.Continue_This_Position__c = 'Yes';
        campaignMemberList.add(campaignMember1);
        insert campaignMemberList;
        
        campaignMemberIdSet.add(campaignMemberList[0].Id);
        campaignMemberIdSet.add(campaignMemberList[1].Id);

        String strCampaigns = string.valueOf(campaignMember.Id) + ',' + string.valueOf(campaignMember1.Id);

        test.startTest();
            Pagereference joinMemberShipPage = Page.VolunteerRenewal_MembershipInformation;
            joinMemberShipPage.getParameters().put('ContactId', contact.Id);
            joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
            joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
            Test.setCurrentPage(joinMemberShipPage);

            VolunteerRenewal_MemberInfoController volunteerRenewalJoinMembershipInfoController = new VolunteerRenewal_MemberInfoController();
            Date myDate = date.newinstance(1960, 2, 17);

            volunteerRenewalJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/1990');
            volunteerRenewalJoinMembershipInfoController.gender = 'male';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'true';
            volunteerRenewalJoinMembershipInfoController.booleanTermsAndConditions = true;
            volunteerRenewalJoinMembershipInfoController.booleanContactPhotoOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactEmailOptIn = false;
            volunteerRenewalJoinMembershipInfoController.mobilePhone = '2425435';
            volunteerRenewalJoinMembershipInfoController.preferredPhone = 'home';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'home';

            volunteerRenewalJoinMembershipInfoController.streetLine1 = 'streetLine1';
            volunteerRenewalJoinMembershipInfoController.streetLine2 = 'streetLine2';
            volunteerRenewalJoinMembershipInfoController.city = 'Pune';
            volunteerRenewalJoinMembershipInfoController.zipCode = '12345';
            volunteerRenewalJoinMembershipInfoController.country = 'USA';
            volunteerRenewalJoinMembershipInfoController.state = 'CA';

            volunteerRenewalJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
            volunteerRenewalJoinMembershipInfoController.booleanGrantRequested =false;

            volunteerRenewalJoinMembershipInfoController.getGenders();
            volunteerRenewalJoinMembershipInfoController.getPreferredEmails();
            volunteerRenewalJoinMembershipInfoController.getPreferredPhones();
            volunteerRenewalJoinMembershipInfoController.getmembershipProductList();

            List<PricebookEntry > PricebookEntryList = VolunteerRegistrationUtilty.getPricebookEntryList();

            PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;      
            volunteerRenewalJoinMembershipInfoController.membershipProduct = newpricebook.Id;      


            Pagereference pageReference = volunteerRenewalJoinMembershipInfoController.submit();


            map<Id, CampaignMember> updatedCampaignMemberMap = new map<Id, CampaignMember>([
                Select Id, Membership__c, ContactId, CampaignId, Display_Renewal__c
                  From CampaignMember 
                 where Id IN :campaignMemberIdSet
            ]);

            Opportunity createdOpportunity = [select Id, Name, Renewal__c, Background_Check__c, Membership_Status__c from Opportunity where Id = : oldMembershipOpportunity.Id limit 1];

            system.assertEquals(updatedCampaignMemberMap.get(campaignMember.Id).Membership__c, oldMembershipOpportunity.Id);
            system.assertEquals(updatedCampaignMemberMap.get(campaignMember.Id).Display_Renewal__c, false);

            if(pageReference.getUrl() !=null && pageReference.getUrl().contains('?')){
                String pagereferenceURL = pageReference.getUrl();
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                //system.assertEquals(pageName,'VOLUNTEERRENEWAL_PAYMENT');
            }
            system.assertNotEquals(pageReference.getParameters().get('OpportunityId'), '');
            system.assertEquals(pageReference.getParameters().get('OpportunityId'), null);

        test.stopTest();
    }

    @isTest(seeAllData = true)
    static void test_101_Scenario7() {

        set<Id> campaignMemberIdSet = new set<Id>();
        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Account insertedCouncilAccount = new Account();
        if(accountList != null && accountList.size() > 0) {
            for(Account insertedCouncil : accountList) {
                if(insertedCouncil.Id != null && insertedCouncil.RecordTypeId == rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE))
                    insertedCouncilAccount = insertedCouncil;
            }
        }

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(insertedCouncilAccount.Id, true);

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Birthdate = system.today().addYears(-15);
        contact.HomePhone = '9999999999';
        insert contact;

        Campaign membershipCampaign = rC_GSATests.initializeCampaign('Lifetime Membership', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        membershipCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.STANDARD_CAMPAIGN_RECORDTYPE);
        membershipCampaign.IsActive = true;
        membershipCampaign.Display_on_Website__c = true;
        membershipCampaign.Program_Grade_Level__c = '1-Daisy';
        upsert membershipCampaign;

        Background_Check__c BC = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Status__c = 'Eligible',
            Background_Check_Complete_Date__c = date.today().addDays(10)//lifetimePricebookEntry.Product2.rC_Giving__Start_Date__c.addDays(10)
        );
        insert BC;

        Opportunity oldMembershipOpportunity = rC_GSATests.initializeOpportunity(householdAccount, membershipCampaign, false);
        oldMembershipOpportunity.Background_Check__c = BC.Id;
        oldMembershipOpportunity.Type = 'Lifetime Membership';
        oldMembershipOpportunity.Contact__c = contact.Id;
        oldMembershipOpportunity.StageName = 'Open';
        insert oldMembershipOpportunity;

        List<Pricebook2> pricebookList = [Select Name, IsActive, IsStandard, Id From Pricebook2 where Name = 'Girl Scouts USA' or IsStandard = true];
        Pricebook2 pricebookIsStandard;
        Pricebook2 pricebookGSA;

        for(Pricebook2 pricebook : pricebookList){
            if(pricebook.IsStandard)
                pricebookIsStandard = pricebook.clone(true, true, false, false);
            
            if(pricebook.Name.equalsIgnoreCase('Girl Scouts USA'))
                pricebookGSA = pricebook.clone(true, true, false, false);
        }

        Product2 newProduct = new Product2(IsActive = true, Name = 'Lifetime Membership (Valid 10/1/2014 - 9/30/2050)',  rC_Giving__Start_Date__c = date.today().addDays(5), rC_Giving__End_Date__c = Date.today().addYears(20));
        newProduct.RecordTypeId = rC_GSATests.getProductRecordTypeId(rC_GSATests.MEMBERSHIP_RECORDTYPE);
        insert newProduct;

        List<PricebookEntry> pricebookEntryList1 = new List<PricebookEntry>();

        PricebookEntry pricebookEntryIsStandard = new PricebookEntry(Product2Id = newProduct.Id, Pricebook2Id = pricebookIsStandard.Id, IsActive = true, UnitPrice = 1.0);
        pricebookEntryList1.add(pricebookEntryIsStandard);
        PricebookEntry lifetimePricebookEntry = new PricebookEntry(Product2Id = newProduct.Id, Pricebook2Id = pricebookGSA.Id, IsActive = true, UnitPrice = 1.0);
        pricebookEntryList1.add(lifetimePricebookEntry);
        insert pricebookEntryList1;

        OpportunityLineItem oldOpportunityLineItem = new OpportunityLineItem(
            PricebookEntryId = pricebookEntryList1[1].Id, 
            OpportunityId = oldMembershipOpportunity.Id, 
            Quantity = 10, TotalPrice = 100);
        insert oldOpportunityLineItem;

        BC.Background_Check_Status__c = 'Expired';
        BC.Background_Check_Complete_Date__c = Date.today().addDays(-10);
        update BC;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        insert parentCampaign;

        List<Campaign> campaignList = new List<Campaign>();

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign.Program_Grade_Level__c = '1-Daisy';
        campaign.Background_Check_Needed__c = true;
        campaignList.add(campaign);

        Campaign campaign2 = rC_GSATests.initializeCampaign('Join Membership Test Campaign2', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        campaign2.Program_Grade_Level__c = '1-Daisy';
        campaign2.Background_Check_Needed__c = true;
        campaignList.add(campaign2);
        insert campaignList;

        List<CampaignMember> campaignMemberList = new List<CampaignMember>();
        
        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        campaignMember.Membership__c = oldMembershipOpportunity.Id;
        campaignMember.Active__c = true;
        campaignMemberList.add(campaignMember);
        
        campaignMemberIdSet.add(campaignMember.Id);
        CampaignMember campaignMember1 = new CampaignMember(ContactId = contact.Id, CampaignId= campaign2.Id);
        campaignMember1.Active__c = true;
        campaignMemberList.add(campaignMember1);
        insert campaignMemberList;

        campaignMemberIdSet.add(campaignMember1.Id);

        String strCampaigns = string.valueOf(campaignMember.Id) + ',' + string.valueOf(campaignMember1.Id);

        test.startTest();
            Pagereference joinMemberShipPage = Page.VolunteerRenewal_MembershipInformation;//new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
            joinMemberShipPage.getParameters().put('ContactId', contact.Id);
            joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
            joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
            Test.setCurrentPage(joinMemberShipPage);

            VolunteerRenewal_MemberInfoController volunteerRenewalJoinMembershipInfoController = new VolunteerRenewal_MemberInfoController();
            Date myDate = date.newinstance(1960, 2, 17);

            volunteerRenewalJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/1990');
            volunteerRenewalJoinMembershipInfoController.gender = 'male';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'true';
            volunteerRenewalJoinMembershipInfoController.booleanTermsAndConditions = true;
            volunteerRenewalJoinMembershipInfoController.booleanContactPhotoOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
            volunteerRenewalJoinMembershipInfoController.booleanContactEmailOptIn = false;
            volunteerRenewalJoinMembershipInfoController.mobilePhone = '2425435';
            volunteerRenewalJoinMembershipInfoController.preferredPhone = 'home';
            volunteerRenewalJoinMembershipInfoController.preferredEmail = 'home';

            volunteerRenewalJoinMembershipInfoController.streetLine1 = 'streetLine1';
            volunteerRenewalJoinMembershipInfoController.streetLine2 = 'streetLine2';
            volunteerRenewalJoinMembershipInfoController.city = 'Pune';
            volunteerRenewalJoinMembershipInfoController.zipCode = '12345';
            volunteerRenewalJoinMembershipInfoController.country = 'USA';
            volunteerRenewalJoinMembershipInfoController.state = 'CA';

            volunteerRenewalJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
            volunteerRenewalJoinMembershipInfoController.booleanGrantRequested =false;

            volunteerRenewalJoinMembershipInfoController.getGenders();
            volunteerRenewalJoinMembershipInfoController.getPreferredEmails();
            volunteerRenewalJoinMembershipInfoController.getPreferredPhones();
            volunteerRenewalJoinMembershipInfoController.getmembershipProductList();

            List<PricebookEntry > PricebookEntryList = VolunteerRegistrationUtilty.getPricebookEntryList();

            PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;      
            volunteerRenewalJoinMembershipInfoController.membershipProduct = newpricebook.Id;      

            Pagereference pageReference = volunteerRenewalJoinMembershipInfoController.submit();

            map<Id, CampaignMember> updatedCampaignMemberMap = new map<Id, CampaignMember>([
                Select Id, Membership__c, ContactId, CampaignId, Display_Renewal__c
                  From CampaignMember 
                 where Id IN :campaignMemberIdSet
            ]);

            Opportunity createdOpportunity = [select Id, Name, Renewal__c, Background_Check__c, Membership_Status__c from Opportunity where Id = : oldMembershipOpportunity.Id limit 1];

            system.assertEquals(updatedCampaignMemberMap.get(campaignMember1.Id).Membership__c, oldMembershipOpportunity.Id);
            system.assertEquals(updatedCampaignMemberMap.get(campaignMember1.Id).Display_Renewal__c, false);

            if(pageReference.getUrl() !=null && pageReference.getUrl().contains('?')){
                String pagereferenceURL = pageReference.getUrl();
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                //system.assertEquals(pageName,'VOLUNTEERRENEWAL_PAYMENT');
            }
            //system.assertEquals(createdOpportunity.Membership_Status__c, 'Background Check');
            system.assertEquals(pageReference.getParameters().get('OpportunityId'), null);
           // system.assertEquals(pageReference.getParameters().get('OpportunityId'), updatedCampaignMemberMap.get(campaignMember1.Id).Membership__c);

        test.stopTest();
    }
    


    @isTest(seeAllData = true)
    static void rc_104_nextButtonLeadingToDemographicsInformationPage() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        
        Account accountCouncil = rC_GSATests.initializeAccount(false);
        accountCouncil.name = 'CouncilAccount';
        accountCouncil.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncil;
        
        Contact contact = rC_GSATests.initializeContact(accountCouncil,true);  

        Campaign campaign = rC_GSATests.initializeCampaign('TestCampaign',null,accountCouncil.Id,'11111', false);
        campaign.Event_Code__c = '12345';
        insert campaign;
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id,campaign.Id,true);
        boolean originalDisableAll;

        Opportunity opportunity = new Opportunity(
            RecordTypeId = VolunteerRenewalUtility.getOpportunityRecordTypeId(VolunteerRenewalUtility.OPPORTUNITY_DONATION_RECORDTYPE), 
            AccountId = accountCouncil.Id, 
            CampaignId = campaign.Id, 
            rC_Giving__Giving_Amount__c = 0, 
            StageName = 'Open',
            CloseDate = System.today()
        );
        insert opportunity;

        test.startTest();
        Pagereference VolunteerPaymentProcessingPage = Page.VolunteerRenewal_Payment;//new Pagereference('/apex/VolunteerRenewal_Payment');
        VolunteerPaymentProcessingPage.getParameters().put('ContactId', contact.Id);
        VolunteerPaymentProcessingPage.getParameters().put('OpportunityId', opportunity.Id);
        VolunteerPaymentProcessingPage.getParameters().put('CampaignMemberIds', campaignMember.Id);
        test.setCurrentPage(VolunteerPaymentProcessingPage);
        VolunteerRenewal_PaymentController volunteerRenewalPaymentProcessingController = new VolunteerRenewal_PaymentController();
        volunteerRenewalPaymentProcessingController.firstName = 'firstName';
        volunteerRenewalPaymentProcessingController.lastName = 'lastName';
        volunteerRenewalPaymentProcessingController.address = 'testAddress';
        volunteerRenewalPaymentProcessingController.city = 'testCity';
        volunteerRenewalPaymentProcessingController.country = 'testCountry';
        volunteerRenewalPaymentProcessingController.state = 'testState';
        volunteerRenewalPaymentProcessingController.zipCode= 'testZipCode';
        volunteerRenewalPaymentProcessingController.cardHolderName= 'testCard';
        volunteerRenewalPaymentProcessingController.cardNumber= '4111111111111111';
        volunteerRenewalPaymentProcessingController.expMonth= '01';
        volunteerRenewalPaymentProcessingController.expYear= '19';
        volunteerRenewalPaymentProcessingController.securityCode= '123';
        volunteerRenewalPaymentProcessingController.acceptGSPromiseAndLaw= true;
        volunteerRenewalPaymentProcessingController.amountValue= 15;
        volunteerRenewalPaymentProcessingController.createTransactionRecord();
        
        rC_Giving__Opportunity_Setting__c setting = rC_Giving__Opportunity_Setting__c.getInstance();
        setting = (setting != null) ? setting : new rC_Giving__Opportunity_Setting__c();
        originalDisableAll = setting.rC_Giving__Disable_All__c;
        setting.rC_Giving__Disable_All__c = true;
        upsert setting;

        volunteerRenewalPaymentProcessingController.fillPaymentMailingAddress();
        volunteerRenewalPaymentProcessingController.getlistexpYear();
        volunteerRenewalPaymentProcessingController.getlistCountryItems();
        volunteerRenewalPaymentProcessingController.getlistStateItems();
        volunteerRenewalPaymentProcessingController.getlistexpMonth();

        Pagereference pagerefernce = volunteerRenewalPaymentProcessingController.processMyOrder();
        system.debug('***pagerefernceINController***'+pagerefernce);
        if(string.valueOf(pagerefernce) !=null && string.valueOf(pagerefernce).contains('?')){
                String pagereferenceURL = string.valueOf(pagerefernce);
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEERRENEWAL_DEMOGRAPHICSINFOPAGE');
            }
        setting.rC_Giving__Disable_All__c = originalDisableAll;
        update setting;

        test.stopTest();
    }

     @isTest(seeAllData = true)
    static void test_105_backgroundCheckNotExpired() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Race__c = 'Asian';
        insert contact;
        
        Campaign campaign = rC_GSATests.initializeCampaignNew(councilAccount.Id, '12345', false);
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id,campaign.Id,true);
        
        Background_Check__c backgroundCheck = new Background_Check__c(Background_Check_Complete_Date__c = system.today().addYears(5),
                                                Contact__c = contact.Id);
        insert backgroundCheck; 
        
        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.Contact__c = contact.Id;
        opportunity.Background_Check__c = backgroundCheck.Id;
        insert opportunity;
        
        Pricebook2 pricebook2 = rC_GSATests.initializePricebook2( true);
        Pricebook2 standardPriceBook = [
            select id
              from Pricebook2
             where isStandard = true
             limit 1
        ];
        Product2 product2 = rC_GSATests.initializeProduct2(false);
        product2.rC_Giving__Start_Date__c = system.today().addYears(5);
        insert product2;
        
        PricebookEntry standardPricebookEntry = rC_GSATests.initializePricebookEntry(standardPriceBook,product2, true);
        OpportunityLineItem opportunityLineItem =  rC_GSATests.initializeOpportunityLineItem(standardPricebookEntry, opportunity, true);
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        Test.startTest();
        //system.runAs(portalUser) {
            Pagereference DemographicsInformation = Page.VolunteerRenewal_DemographicsInfoPage;//new Pagereference('/apex/VolunteerRenewal_DemographicsInfoPage');
            DemographicsInformation.getParameters().put('ContactId', contact.Id);
            DemographicsInformation.getParameters().put('CouncilId', councilAccount.Id);
            DemographicsInformation.getParameters().put('OpportunityId', opportunity.Id);
            DemographicsInformation.getParameters().put('CampaignMemberIds', campaignMember.Id);
            Test.setCurrentPage(DemographicsInformation);
            VolunteerRenewal_DemographInfoController volunteerRenewal = new VolunteerRenewal_DemographInfoController();
            volunteerRenewal.selectedRace = 'Asian';
            volunteerRenewal.getEthnicityOptionList();
            volunteerRenewal.selectedEthnicity = 'Hispanic';
            volunteerRenewal.occupation = 'Enginner';
            volunteerRenewal.adultNoOfYearsInGS = '4';
            volunteerRenewal.girlNoOfYearsInGS = '5';
            volunteerRenewal.lstSelectedCampaignFields.add(new SelectOption('Asian','Asian'));
            Pagereference pagereference = volunteerRenewal.submit();
            system.assertEquals(string.valueOf(pagereference).contains('isBackgroundCheckFlag=true'),true);
        test.stopTest();
       // }
    }
     @isTest(seeAllData = true)
    static void test_105_backgroundCheckExpired() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Race__c = 'Asian';
        insert contact;
        
        Campaign campaign = rC_GSATests.initializeCampaignNew(councilAccount.Id, '12345', false);
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id,campaign.Id,true);
        
        Background_Check__c backgroundCheck = new Background_Check__c(Background_Check_Complete_Date__c =  date.newinstance(2011,5,23),
                                                Contact__c = contact.Id);
        insert backgroundCheck; 
        
        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.Contact__c = contact.Id;
        opportunity.Background_Check__c = backgroundCheck.Id;
        insert opportunity;
        
        Pricebook2 pricebook2 = rC_GSATests.initializePricebook2( true);
        Pricebook2 standardPriceBook = [
            select id
              from Pricebook2
             where isStandard = true
             limit 1
        ];
        Product2 product2 = rC_GSATests.initializeProduct2(false);
        product2.rC_Giving__Start_Date__c = system.today().addYears(5);
        insert product2;
        
        PricebookEntry standardPricebookEntry = rC_GSATests.initializePricebookEntry(standardPriceBook,product2, true);
        OpportunityLineItem opportunityLineItem =  rC_GSATests.initializeOpportunityLineItem(standardPricebookEntry, opportunity, true);
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        Test.startTest();
       // system.runAs(portalUser) {
            Pagereference DemographicsInformation = Page.VolunteerRenewal_DemographicsInfoPage;//new Pagereference('/apex/VolunteerRenewal_DemographicsInfoPage');
            DemographicsInformation.getParameters().put('ContactId', contact.Id);
            DemographicsInformation.getParameters().put('CouncilId', councilAccount.Id);
            DemographicsInformation.getParameters().put('OpportunityId', opportunity.Id);
            DemographicsInformation.getParameters().put('CampaignMemberIds', campaignMember.Id);
            Test.setCurrentPage(DemographicsInformation);
            VolunteerRenewal_DemographInfoController volunteerRenewal = new VolunteerRenewal_DemographInfoController();
            volunteerRenewal.selectedRace = 'Asian';
            volunteerRenewal.selectedEthnicity = 'Hispanic';
            volunteerRenewal.occupation = 'Engineer';
            volunteerRenewal.adultNoOfYearsInGS = '4';
            volunteerRenewal.girlNoOfYearsInGS = '5';
            volunteerRenewal.lstSelectedCampaignFields.add(new SelectOption('Asian','Asian'));
            Pagereference pagereference = volunteerRenewal.submit();
            system.assertEquals(string.valueOf(pagereference).contains('isBackgroundCheckFlag=false'),true);
        test.stopTest();
       // }
    }
    
    static testMethod void test_105_redirectToDemographicsThankYouPageWhenBackgroundCheckIsNotExpired() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Campaign campaign = rC_GSATests.initializeCampaignNew(councilAccount.Id, '12345', false);
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id,campaign.Id,true);
        
        Background_Check__c backgroundCheck = new Background_Check__c(Background_Check_Complete_Date__c =  date.newinstance(2011,5,23),
                                                Contact__c = contact.Id);
        insert backgroundCheck; 
        
        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.Contact__c = contact.Id;
        opportunity.Background_Check__c = backgroundCheck.Id;
        insert opportunity;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        Test.startTest();
     //   system.runAs(portalUser) {
            Pagereference DemographicsInformation = Page.VolunteerRenewal_DemographicsThankYou;//new Pagereference('/apex/VolunteerRenewal_DemographicsThankYou');
            DemographicsInformation.getParameters().put('ContactId', contact.Id);
            DemographicsInformation.getParameters().put('CouncilId', councilAccount.Id);
            DemographicsInformation.getParameters().put('CampaignMemberIds', campaignMember.Id);
            DemographicsInformation.getParameters().put('isBackgroundCheckFlag','true');
            Test.setCurrentPage(DemographicsInformation);
            VolunteerRenewal_DemoThankyouController volunteerRenewalThankYou = new VolunteerRenewal_DemoThankyouController();
            system.assert(volunteerRenewalThankYou.isBackgroundCheckFlag == true);
        test.stopTest();
       // }
    }
    
    static testMethod void test_105_redirectToDemographicsThankYouPageWhenBackgroundCheckIsExpired() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Campaign campaign = rC_GSATests.initializeCampaignNew(councilAccount.Id, '12345', false);
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id,campaign.Id,true);
        
        Background_Check__c backgroundCheck = new Background_Check__c(Background_Check_Complete_Date__c =  date.newinstance(2011,5,23),
                                                Contact__c = contact.Id);
        insert backgroundCheck; 
        
        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.Contact__c = contact.Id;
        opportunity.Background_Check__c = backgroundCheck.Id;
        insert opportunity;
        
        User portalUser = new User( email='test-user@email.com', 
                                    contactid = contact.id, 
                                    profileid = profileid,
                                    UserName='test-user@email.com', 
                                    alias='tuser1', 
                                    CommunityNickName='tuser1', 
                                    TimeZoneSidKey='America/New_York', 
                                    LocaleSidKey='en_US', 
                                    EmailEncodingKey='ISO-8859-1', 
                                    LanguageLocaleKey='en_US',
                                    FirstName = 'Test',
                                    LastName = 'User' );
        insert portalUser;
        Test.startTest();
     //   system.runAs(portalUser) {
            Pagereference DemographicsInformation = Page.VolunteerRenewal_DemographicsThankYou;//new Pagereference('/apex/VolunteerRenewal_DemographicsThankYou');
            DemographicsInformation.getParameters().put('ContactId', contact.Id);
            DemographicsInformation.getParameters().put('CouncilId', councilAccount.Id);
            DemographicsInformation.getParameters().put('CampaignMemberIds', campaignMember.Id);
            DemographicsInformation.getParameters().put('isBackgroundCheckFlag','false');
            Test.setCurrentPage(DemographicsInformation);
            VolunteerRenewal_DemoThankyouController volunteerRenewalThankYou = new VolunteerRenewal_DemoThankyouController();
            system.assert(volunteerRenewalThankYou.isBackgroundCheckFlag == false);
        test.stopTest();
        //}
    }
    
   
    
    static testMethod void test_120_SearchForTroopRoleByName() {
        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;

        Contact girlContact = rC_GSATests.initializeContact(householdAccount, false);
        girlContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert girlContact;

        Contact parentContact = rC_GSATests.initializeContact(householdAccount, false);
        parentContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert parentContact;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('Troop Group',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        insert troopOrGroupVolunteerProject;

        Test.startTest();

        Pagereference TroopGroupRoleSearchPage = Page.GirlCatalog_TroopGroupRoleSearch;//new Pagereference('/apex/GirlCatalog_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', girlContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        update troopOrGroupVolunteerProject;
        
        GirlCatalog_TroopSearchController girlTroopGroupRoleSearch = new GirlCatalog_TroopSearchController();
        girlTroopGroupRoleSearch.troopOrGroupName = 'Troop Group';
        
        girlTroopGroupRoleSearch.getPageSizeOptions();
        girlTroopGroupRoleSearch.getRadiusInMiles();
        
        girlTroopGroupRoleSearch.selectedRadius = '5';
        
        girlTroopGroupRoleSearch.selectedPageSize = '1';
        girlTroopGroupRoleSearch.selectedPageNumber = '1';
        
        girlTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        girlTroopGroupRoleSearch.nextButtonClick();
        girlTroopGroupRoleSearch.previousButtonClick();
        
        girlTroopGroupRoleSearch.getItems();
        girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);

        Test.stopTest();
    }

    static testMethod void test_120_SearchForTroopRoleByZipCode() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        List<Zip_Code__c> zipCodeList = new List<Zip_Code__c>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;

        Contact girlContact = rC_GSATests.initializeContact(householdAccount, false);
        girlContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert girlContact;

        Contact parentContact = rC_GSATests.initializeContact(householdAccount, false);
        parentContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert parentContact;
        
        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '19312';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        zipCodeList.add(zipCodeInstance1);

        Zip_Code__c zipCodeInstance2 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance2.Zip_Code_Unique__c = '19373';
        zipCodeInstance2.name = '19373';
        zipCodeInstance2.geo_location__Latitude__s =  39.89000;
        zipCodeInstance2.geo_location__Longitude__s = -75.53000;
        zipCodeList.add(zipCodeInstance2);

        Zip_Code__c zipCodeInstance3 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance3.Zip_Code_Unique__c = '19409';
        zipCodeInstance3.name = '19409';
        zipCodeInstance3.geo_location__Latitude__s =  40.14000;
        zipCodeInstance3.geo_location__Longitude__s = -75.36000;
        zipCodeList.add(zipCodeInstance3);
        insert zipCodeList;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj1',null, councilAccount.Id, zipCodeList[1].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = zipCodeList[1].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject1);

        Campaign troopOrGroupVolunteerProject2 = rC_GSATests.initializeCampaign('TroopGroupVolProj2',null, councilAccount.Id, zipCodeList[2].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject2.Display_on_Website__c = true;
        troopOrGroupVolunteerProject2.Zip_Code__c = zipCodeList[2].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject2);
        insert volunteerProjectCampaignList;

        Test.startTest();

        Pagereference TroopGroupRoleSearchPage = Page.GirlCatalog_TroopGroupRoleSearch;//new Pagereference('/apex/GirlCatalog_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', girlContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('ParentContactId', parentContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerProjectCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        GirlCatalog_TroopSearchController girlTroopGroupRoleSearch = new GirlCatalog_TroopSearchController();
        girlTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;

        girlTroopGroupRoleSearch.getPageSizeOptions();
        girlTroopGroupRoleSearch.getRadiusInMiles();

        girlTroopGroupRoleSearch.selectedRadius = '20';
        girlTroopGroupRoleSearch.Grade = '--None--';
        girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        girlTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked = true;

        system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);
        system.assertNotEquals(girlTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignDistance, girlTroopGroupRoleSearch.parentCampaignWrapperList[1].campaignDistance);

        Test.stopTest();
    }

    static testMethod void test_120_ClearSearch() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.GirlCatalog_TroopGroupRoleSearch;//new Pagereference('/apex/GirlCatalog_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        GirlCatalog_TroopSearchController girlTroopGroupRoleSearch = new GirlCatalog_TroopSearchController();
        girlTroopGroupRoleSearch.troopOrGroupName = 'TroopGroupVolProj';
        girlTroopGroupRoleSearch.zipCode = '11111';
        girlTroopGroupRoleSearch.selectedRadius = '5';
        
        girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        girlTroopGroupRoleSearch.selectedPageSize = '3';
        girlTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        girlTroopGroupRoleSearch.nextButtonClick();
        girlTroopGroupRoleSearch.previousButtonClick();
        girlTroopGroupRoleSearch.clearSelections();

        system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        system.assertNotEquals(girlTroopGroupRoleSearch.zipCode, '11111');
        system.assertNotEquals(girlTroopGroupRoleSearch.troopOrGroupName, 'TroopGroupVolProj');

        Test.stopTest();
    }

    static testMethod void test_120_SeeTroopNamesSameAsMySpelling() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);

        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;

        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;

        Test.startTest();

        Pagereference TroopGroupRoleSearchPage = Page.GirlCatalog_TroopGroupRoleSearch;//new Pagereference('/apex/GirlCatalog_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerProjectCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        GirlCatalog_TroopSearchController girlTroopGroupRoleSearch = new GirlCatalog_TroopSearchController();
        girlTroopGroupRoleSearch.troopOrGroupName = 'TroopGroupVolProj';
        girlTroopGroupRoleSearch.zipCode = '11111';
        girlTroopGroupRoleSearch.selectedRadius = '5';

        List<String> jasonString = GirlCatalog_TroopSearchController.searchCampaingNames('Troop');
        system.assertNotEquals(jasonString.size(), 0);

        Test.stopTest();
    }

    static testMethod void test_120_TroopNotFoundSearchingByName() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);

        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);

        Campaign irmCampaign = rC_GSATests.initializeCampaign('Individually Registered Member',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        irmCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        irmCampaign.Display_on_Website__c = true;
        irmCampaign.Participation__c = 'IRM';
        volunteerProjectCampaignList.add(irmCampaign);
        insert volunteerProjectCampaignList;

        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;

        Test.startTest();

        Pagereference TroopGroupRoleSearchPage = Page.GirlCatalog_TroopGroupRoleSearch;//new Pagereference('/apex/GirlCatalog_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerProjectCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        try{
            GirlCatalog_TroopSearchController girlTroopGroupRoleSearch = new GirlCatalog_TroopSearchController();
            girlTroopGroupRoleSearch.troopOrGroupName = 'Alexabc';
            girlTroopGroupRoleSearch.selectedRadius = '5';
            girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

            system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() == 2);
        }
        catch(Exception e){
            system.debug('Exception==>'+e);
        }
        Test.stopTest();
    }

    static testMethod void test_120_TroopNotFoundSearchingbyRadius() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        List<Zip_Code__c> Zip_CodeList = new List<Zip_Code__c>();
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCode.Zip_Code_Unique__c = '80307';
        zipCode.geo_location__Latitude__s = 45.49090;
        zipCode.geo_location__Longitude__s = -92.78620;
        Zip_CodeList.add(zipCode);
        insert Zip_CodeList;
        
        Zip_Code__c zipCode1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCode1.geo_location__Latitude__s = 45.49090;
        zipCode1.geo_location__Longitude__s = -92.78620;
        //Zip_CodeList.add(zipCode1);
        //insert Zip_CodeList;
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, null, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        //troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);

        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, null, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);

        Campaign irmCampaign = rC_GSATests.initializeCampaign('Individually Registered Member',null, councilAccount.Id, null, false);
        irmCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        irmCampaign.Display_on_Website__c = true;
        irmCampaign.Participation__c = 'IRM';
        volunteerProjectCampaignList.add(irmCampaign);
        insert volunteerProjectCampaignList;

        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;

        Test.startTest();

        Pagereference TroopGroupRoleSearchPage = Page.GirlCatalog_TroopGroupRoleSearch;//new Pagereference('/apex/GirlCatalog_TroopGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerProjectCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        try{
            GirlCatalog_TroopSearchController girlTroopGroupRoleSearch = new GirlCatalog_TroopSearchController();
            girlTroopGroupRoleSearch.zipCode = '80307';
            girlTroopGroupRoleSearch.selectedRadius = '5';
            girlTroopGroupRoleSearch.Grade = '--NONE--';
            girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
            system.debug('--------------'+girlTroopGroupRoleSearch.parentCampaignWrapperList.size());
            system.assertEquals(girlTroopGroupRoleSearch.parentCampaignWrapperList.size(), 2);
        }
        catch(Exception e){
            system.debug('Exception===>'+e);
        }
        Test.stopTest();
    }
     

    
    static testMethod void rc_152_CreateTask_afterUpdateBackgroundCheck() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId = rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;

        Campaign campaign = rC_GSATests.initializeCampaign('Test Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        Background_Check__c backgroundCheck = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Status__c = 'Pending',
            Background_Check_Start_Date__c = Date.today().addYears(1)
        );
        insert backgroundCheck;

        User user = [select Id, Name from User where IsActive = true and Profile.Name = 'System Administrator' limit 1];

        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.Background_Check__c = backgroundCheck.Id;
        opportunity.Contact__c = contact.Id;
        opportunity.OwnerId = user.Id;
        insert opportunity;

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Special_Handling__c = true;
        campaignMember.Active__c = false;
        campaignMember.Membership__c = opportunity.Id;
        insert campaignMember;

        Task[] taskList = [Select Id, WhoId, WhatId, OwnerId, IsClosed From Task where WhatId = :campaign.Id and WhoId = :contact.Id];
        Integer oldTaskCount = taskList.size();

        backgroundCheck.Background_Check_Status__c = 'Eligible';
        update backgroundCheck;

        taskList = [Select Id, WhoId, WhatId, OwnerId, IsClosed From Task where WhatId = :campaign.Id and WhoId = :contact.Id];

        Contact contactWho = [select Id, Name from Contact where Id = :contact.Id];
        
        String taskSubject = 'Special Handling - ' + contactWho.Name;
        
        Task task = [
            Select Id
                 , WhoId
                 , WhatId
                 , OwnerId
                 , IsClosed
                 , Subject 
              From Task 
             where WhatId = :campaign.Id 
               and WhoId = :contact.Id 
               and Subject = : taskSubject
               and IsClosed = false
        ];

        system.assert(taskList.size() > oldTaskCount);
        system.assertEquals(task.OwnerId, opportunity.OwnerId);

    }

    static testMethod void rc_152_CreateTask_afterUpdateCampaignMember() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId = rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;

        Campaign campaign = rC_GSATests.initializeCampaign('Test Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;
        
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Special_Handling__c = true;
        campaignMember.Active__c = false;
        insert campaignMember;

        Background_Check__c backgroundCheck = new Background_Check__c(
            Contact__c = contact.Id,
            Background_Check_Status__c = 'Eligible',
            Background_Check_Start_Date__c = Date.today().addYears(1)
        );
        insert backgroundCheck;

        User user = [select Id, Name from User where IsActive = true and Profile.Name = 'System Administrator' limit 1];

        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.Background_Check__c = backgroundCheck.Id;
        opportunity.OwnerId = user.Id;
        opportunity.Contact__c = contact.Id;
        insert opportunity;

        campaignMember.Membership__c = opportunity.Id;

        Task[] taskList = [Select Id, WhoId, WhatId, OwnerId, IsClosed From Task where WhatId = :campaign.Id and WhoId = :contact.Id];
        Integer oldTaskCount = taskList.size();

        update campaignMember;

        taskList = [Select Id, WhoId, WhatId, OwnerId, IsClosed From Task where WhatId = :campaign.Id and WhoId = :contact.Id];
        
        Contact contactWho = [select Id, Name from Contact where Id = :contact.Id];
        
        String taskSubject = 'Opportunity selected Volunteer Role that requires Special Handling - ' + contactWho.Name;
        
        Task task = [
            Select Id
                 , WhoId
                 , WhatId
                 , OwnerId
                 , IsClosed
                 , Subject 
              From Task 
             where WhatId = :campaign.Id 
               and WhoId = :contact.Id 
               and Subject = : taskSubject
               and IsClosed = false
        ];

        system.assert(taskList.size() > oldTaskCount);
       
    }
    
    
    
    static testMethod void test_119_SearchForTroopRoleByName(){
        List<Account> accountList = new List<Account>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        //insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('Troop Group',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        insert troopOrGroupVolunteerProject;
        
        Campaign[] campaignList = [Select Id from Campaign where Id = :troopOrGroupVolunteerProject.Id];
        
        Id parentCampaignId =  (campaignList != null && campaignList.size() > 0) ? campaignList[0].Id : '';
        
        Campaign troopGroupVolunteerJob = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob.Display_on_Website__c = true;
        insert troopGroupVolunteerJob;
         
        Test.startTest();
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        troopGroupVolunteerJob.Display_on_Website__c = true;
        update troopGroupVolunteerJob;
        
        VolunteerCatalog_TroopSearchController volTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
        volTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volTroopGroupRoleSearch.selectedRadius = '5';
        
        volTroopGroupRoleSearch.getPageSizeOptions();
        volTroopGroupRoleSearch.getRadiusInMiles();
        
        volTroopGroupRoleSearch.selectedPageSize = '1';
        volTroopGroupRoleSearch.selectedPageNumber = '1';
        
        volTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        volTroopGroupRoleSearch.nextButtonClick();
        volTroopGroupRoleSearch.previousButtonClick();
        
        volTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        volTroopGroupRoleSearch.campaignDetailsId = troopGroupVolunteerJob.Id;
        volTroopGroupRoleSearch.showDetails();

        system.assert(volTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);        
        Test.stopTest();
    }
    
static testMethod void test_119_SearchForTroopRoleByZipCode() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        List<Zip_Code__c> zipCodeList = new List<Zip_Code__c>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.MailingPostalCode = '192';
        insert contact;
        
        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '192';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        zipCodeList.add(zipCodeInstance1);
        
        Zip_Code__c zipCodeInstance2 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance2.Zip_Code_Unique__c = '19373';
        zipCodeInstance2.name = '19373';
        zipCodeInstance2.geo_location__Latitude__s =  39.89000;
        zipCodeInstance2.geo_location__Longitude__s = -75.53000;
        zipCodeList.add(zipCodeInstance2);
        
        Zip_Code__c zipCodeInstance3 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance3.Zip_Code_Unique__c = '19409';
        zipCodeInstance3.name = '19409';
        zipCodeInstance3.geo_location__Latitude__s =  40.14000;
        zipCodeInstance3.geo_location__Longitude__s = -75.36000;
        zipCodeList.add(zipCodeInstance3);
        insert zipCodeList;
        
        Zip_Code__c zipCodeInstance4 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance4.Zip_Code_Unique__c = '19410';
        zipCodeInstance4.name = '19410';
        zipCodeInstance4.geo_location__Latitude__s =  40.14000;
        zipCodeInstance4.geo_location__Longitude__s = -75.36000;
        insert zipCodeInstance4;
        
        Zip_Code__c zipCodeInstance5 = [Select geo_location__Longitude__s
                             , geo_location__Latitude__s
                             , Zip_Code_Unique__c
                             , Name 
                          From Zip_Code__c 
                         where Zip_Code_Unique__c = '19410'];
                         
        system.debug('zipCodeInstance5===> '+zipCodeInstance5);
        system.debug('zipCodeList[0]==>'+zipCodeList[0]+'   zipCodeList[1]'+zipCodeList[1] +'   zipCodeList[2]'+zipCodeList[2]);
        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCodeList[1].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = zipCodeList[1].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject1);
        
        Campaign troopOrGroupVolunteerProject2 = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCodeList[2].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject2.Display_on_Website__c = true;
        troopOrGroupVolunteerProject2.Zip_Code__c = zipCodeList[2].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject2);
        insert volunteerProjectCampaignList;

        //system.debug('volunteerProjectCampaignList[0]==>'+volunteerProjectCampaignList[0]+'   volunteerProjectCampaignList[1]'+volunteerProjectCampaignList[1]);
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign troopGroupVolunteerJob3 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob3.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob3.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob3);
        
        Campaign troopGroupVolunteerJob4 = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob4.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob4.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob4);
        insert volunteerJobsCampaignList;
        
        //system.debug('volunteerJobsCampaignList.zipCode=>'+volunteerJobsCampaignList);
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        VolunteerCatalog_TroopSearchController volunteerTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
        volunteerTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;
        system.debug('volunteerTroopGroupRoleSearch.zipCode=>'+volunteerTroopGroupRoleSearch.zipCode);
        
        volunteerTroopGroupRoleSearch.selectedRadius = '15';
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        volunteerTroopGroupRoleSearch.selectedPageSize = '1';
        volunteerTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        volunteerTroopGroupRoleSearch.nextButtonClick();
        volunteerTroopGroupRoleSearch.previousButtonClick();
        system.debug('volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size()=>'+volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size());
        system.debug('volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size()=>'+volunteerTroopGroupRoleSearch.parentCampaignWrapperList);
        //volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked = true;
        //volunteerTroopGroupRoleSearch.addCampaignMember();
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);
        Test.stopTest();
    }   
    
    
    static testmethod void test_119_SearchByNameWithTwoSameNamesExists(){
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        insert troopOrGroupVolunteerProject;
        
        Campaign[] campaignList = [Select Id from Campaign where Id = :troopOrGroupVolunteerProject.Id];
        Id parentCampaignId =  (campaignList != null && campaignList.size() > 0) ? campaignList[0].Id : '';
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign troopGroupVolunteerJob3 = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob3.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob3.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob3);
        insert volunteerJobsCampaignList;
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        VolunteerCatalog_TroopSearchController volunteerTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';
        
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        volunteerTroopGroupRoleSearch.selectedPageSize = '2';
        volunteerTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        volunteerTroopGroupRoleSearch.nextButtonClick();
        volunteerTroopGroupRoleSearch.previousButtonClick();
        
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        
        Test.stopTest();
    }
    static testMethod void test_119_SearchForTroopRoleByNameAndZipCode() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        List<Zip_Code__c> zipCodeList = new List<Zip_Code__c>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.MailingPostalCode = '19312';
        insert contact;
        
        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '19312';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        zipCodeList.add(zipCodeInstance1);
        
        Zip_Code__c zipCodeInstance2 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance2.Zip_Code_Unique__c = '19373';
        zipCodeInstance2.name = '19373';
        zipCodeInstance2.geo_location__Latitude__s =  39.89000;
        zipCodeInstance2.geo_location__Longitude__s = -75.53000;
        zipCodeList.add(zipCodeInstance2);
        
        Zip_Code__c zipCodeInstance3 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance3.Zip_Code_Unique__c = '19409';
        zipCodeInstance3.name = '19409';
        zipCodeInstance3.geo_location__Latitude__s =  40.14000;
        zipCodeInstance3.geo_location__Longitude__s = -75.36000;
        zipCodeList.add(zipCodeInstance3);
        insert zipCodeList;
        
        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCodeList[1].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = zipCodeList[1].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject1);
        
        Campaign troopOrGroupVolunteerProject2 = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCodeList[2].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject2.Display_on_Website__c = true;
        troopOrGroupVolunteerProject2.Zip_Code__c = zipCodeList[2].Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject2);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign troopGroupVolunteerJob3 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob3.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob3.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob3);
        
        Campaign troopGroupVolunteerJob4 = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob4.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob4.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob4);
        insert volunteerJobsCampaignList;
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        VolunteerCatalog_TroopSearchController volunteerTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
        volunteerTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;
        
        volunteerTroopGroupRoleSearch.selectedRadius = '15';
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);
        system.assertNotEquals(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignDistance, volunteerTroopGroupRoleSearch.parentCampaignWrapperList[2].campaignDistance);
        
        Test.stopTest();
    }
    
    static testMethod void test_119_UnsureOfVolunteerRole() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        Campaign[] campaignList = [Select Id from Campaign where Id = :troopOrGroupVolunteerProject.Id];
        Id parentCampaignId =  (campaignList != null && campaignList.size() > 0) ? campaignList[0].Id : '';
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
        
        List<Campaign> campaignDisplayonWebsiteProjectList = new List<Campaign>();
        
        for(Campaign campaign : volunteerProjectCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteProjectList .add(campaign);
        }
        
        if(!campaignDisplayonWebsiteProjectList .isEmpty())
            update campaignDisplayonWebsiteProjectList ;
            
        VolunteerCatalog_TroopSearchController volunteerTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Unsure';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';
        volunteerTroopGroupRoleSearch.selectedPageSize = '2';
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        
        Test.stopTest();
    }
    
    static testMethod void test_119_ClearSearch() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        Campaign[] campaignList = [Select Id from Campaign where Id = :troopOrGroupVolunteerProject.Id];
        Id parentCampaignId =  (campaignList != null && campaignList.size() > 0) ? campaignList[0].Id : '';
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', parentCampaignId, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', parentCampaignId, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        VolunteerCatalog_TroopSearchController volunteerTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.zipCode = '11111';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';
        
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
        
        volunteerTroopGroupRoleSearch.selectedPageSize = '3';
        volunteerTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        volunteerTroopGroupRoleSearch.nextButtonClick();
        volunteerTroopGroupRoleSearch.previousButtonClick();
        volunteerTroopGroupRoleSearch.clearSelections();
        
        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        system.assertNotEquals(volunteerTroopGroupRoleSearch.zipCode, '11111');
        system.assertNotEquals(volunteerTroopGroupRoleSearch.troopOrGroupName, 'Troop Ledader1');
        
        Test.stopTest();
    }
    
    static testMethod void test_119_SeeTroopNamesSameAsMySpelling() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        VolunteerCatalog_TroopSearchController volunteerTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.zipCode = '11111';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';
        
        List<String> jasonString = VolunteerCatalog_TroopSearchController.searchCampaingNames('Troop Le');
        
        system.assertNotEquals(jasonString.size(), 0);
        
        Test.stopTest();
    }
    
    static testMethod void test_119_TroopNotFoundSearchingByName() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        
        Campaign unsureParentCampaign = rC_GSATests.initializeCampaign('Unsure',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        unsureParentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        unsureParentCampaign.Display_on_Website__c = true;
        volunteerProjectCampaignList.add(unsureParentCampaign);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        
        Campaign unsureChildCampaign = rC_GSATests.initializeCampaign('Unsure', volunteerProjectCampaignList[1].Id, councilAccount.Id, null, false);
        unsureChildCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        unsureChildCampaign.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(unsureChildCampaign);
        insert volunteerJobsCampaignList;
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        try{
            
            List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
            for(Campaign campaign : volunteerJobsCampaignList) {
                campaign.Display_on_Website__c = true;
                campaignDisplayonWebsiteList.add(campaign);
            }
        
            if(!campaignDisplayonWebsiteList.isEmpty())
                update campaignDisplayonWebsiteList;
            
            VolunteerCatalog_TroopSearchController volunteerTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
            volunteerTroopGroupRoleSearch.troopOrGroupName = 'Alex';
            volunteerTroopGroupRoleSearch.selectedRadius = '5';
            volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
            
            system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        }
        catch(Exception e){
            system.debug('$$$$$$$+++++++++++++++===>'+e);
        }
        
        Test.stopTest();
    }
    
    static testMethod void test_119_TroopNotFoundSearchingbyRadius() {
        List<Account> accountList = new List<Account>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        List<Campaign> volunteerProjectCampaignList = new List<Campaign>();
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);
        insert councilAccount;
        
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert householdAccount;
        
        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);
        
        Campaign troopOrGroupVolunteerProject = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        troopOrGroupVolunteerProject.Zip_Code__c = zipCode.Zip_Code_Unique__c;
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject);
        insert volunteerProjectCampaignList;
        
        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob1);
        
        Campaign troopGroupVolunteerJob2 = rC_GSATests.initializeCampaign('Troop Ledader1', volunteerProjectCampaignList[0].Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob2.Display_on_Website__c = true;
        volunteerJobsCampaignList.add(troopGroupVolunteerJob2);
        insert volunteerJobsCampaignList;
        
        Test.startTest();
        
        Pagereference TroopGroupRoleSearchPage = Page.VolunteerCatalog_TroopOrGroupRoleSearch;//new Pagereference('/apex/VolunteerCatalog_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        
        List<Campaign> campaignDisplayonWebsiteList = new List<Campaign>();
        
        for(Campaign campaign : volunteerJobsCampaignList) {
            campaign.Display_on_Website__c = true;
            campaignDisplayonWebsiteList.add(campaign);
        }
        
        if(!campaignDisplayonWebsiteList.isEmpty())
            update campaignDisplayonWebsiteList;
            
        try{
            VolunteerCatalog_TroopSearchController volunteerTroopGroupRoleSearch = new VolunteerCatalog_TroopSearchController();
            volunteerTroopGroupRoleSearch.zipCode = '12345';
            volunteerTroopGroupRoleSearch.selectedRadius = '5';
            volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
            
            system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        }
        catch(Exception e){
            system.debug('$$$$$$$+++++++++++++++===>'+e);
        }
        
        Test.stopTest();
    }
       


    static testMethod void test_185_initiateVolunteerRegistrationGromGirlFlow() {
        Account account = rC_GSATests.initializeAccount(true);
        Contact parentContact = rC_GSATests.initializeParentContact(account,true);
        Test.startTest();
        Pagereference volunteerBasicMembership = Page.Volunteer_BasicMembershipInformation;//new Pagereference('/apex/Volunteer_BasicMembershipInformation');
        volunteerBasicMembership.getParameters().put('ParentContactId', parentContact.Id);
        Test.setCurrentPage(volunteerBasicMembership);
        VolunteerController volunteerController = new VolunteerController();
        system.assertEquals(volunteerController.firstName,parentContact.FirstName);
        system.assertEquals(volunteerController.lastName,parentContact.LastName);  
        test.stopTest();  
    }
}