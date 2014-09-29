@isTest
private class rC_GSATests_00000 {

    private static final map<String, Schema.RecordTypeInfo> CAMPAIGN_MEMBER_RECORDTYPE_INFO_MAP =  CampaignMember.SObjectType.getDescribe().getRecordTypeInfosByName();
    public static final String RT_ADULT_MEMBER_ID = (CAMPAIGN_MEMBER_RECORDTYPE_INFO_MAP.get('Adult Member') != null) ? CAMPAIGN_MEMBER_RECORDTYPE_INFO_MAP.get('Adult Member').getRecordTypeId() : null;

    static testMethod void test_21_Scenario1_EmailAndNameExistOnContact() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        councilAccount.Council_Header_Url__c = 'testUrl';
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId = rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;
        VolunteerController volunteerController = new VolunteerController();
        volunteerController.firstName = contact.FirstName;
        volunteerController.lastName = contact.LastName;
        volunteerController.email = contact.Email;
        volunteerController.confirmationEmail = contact.Email;
        volunteerController.phone = '1234567890';
        volunteerController.zipCode = zipCode.Zip_Code_Unique__c;
        volunteerController.eventCode = '123';

        test.startTest();
        PageReference getStartedThankYouPage = volunteerController.submit();
        if (getStartedThankYouPage != null) {
            Test.setCurrentPage(getStartedThankYouPage);
            VolunteerController.searchRoles();
        }
        Contact matchingContact = volunteerController.getMatchingContact(volunteerController.firstName, volunteerController.lastName, volunteerController.email);
        system.assertNotEquals(matchingContact, null);
        system.assertNotEquals(getStartedThankYouPage, null);
        system.assertEquals(volunteerController.zipCode, matchingContact.MailingPostalCode);

        test.stopTest();
    }

    static testMethod void test_21_Scenario2_EmailAndNameExistOnLead() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        VolunteerController volunteerController = new VolunteerController();
        volunteerController.firstName = 'Test';
        volunteerController.lastName = 'Lead';
        volunteerController.email = 'test@testlead.com';
        volunteerController.confirmationEmail = 'test@testlead.com';
        volunteerController.phone = '1234567890';
        volunteerController.zipCode = zipCode.Zip_Code_Unique__c;
        volunteerController.eventCode = '123';

        Lead lead = rC_GSATests.initializeLead(false);
        lead.FirstName = volunteerController.firstName;
        lead.LastName = volunteerController.lastName;
        lead.Email = volunteerController.email;
        insert lead;

        test.startTest();

        volunteerController.submit();
        Lead convertedLead = [Select Id, IsConverted from Lead where Id = :lead.Id];
        Contact contact = volunteerController.getMatchingContact(volunteerController.firstName, volunteerController.lastName, volunteerController.email);

        system.assertEquals(convertedLead.IsConverted, true);
        system.assertNotEquals(contact, null);
        test.stopTest();
    }

    static testMethod void test_21_Scenario3_EmailAndNameExistOn2Contacts() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact oldContact = rC_GSATests.initializeContact(householdAccount, false);
        oldContact.RecordTypeId = rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        oldContact.FirstName = 'contactFirstName21';
        oldContact.LastName = 'contactLastName21';
        oldContact.rC_Bios__Home_Email__c = 'contactFirstName21.contactLastName21@test.com';
        insert oldContact;

        Contact newContact = rC_GSATests.initializeContact(householdAccount, false);
        newContact.RecordTypeId = rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        newContact.FirstName = 'contactFirstName21';
        newContact.LastName = 'contactLastName21';
        newContact.rC_Bios__Home_Email__c = 'contactFirstName21.contactLastName21@test.com';
        insert newContact;

        Contact secondContact = [select Id, FirstName, LastName, Email from Contact where Id = :newContact.Id];

        VolunteerController volunteerController = new VolunteerController();
        volunteerController.firstName = secondContact.FirstName;
        volunteerController.lastName = secondContact.LastName;
        volunteerController.email = secondContact.Email;
        volunteerController.confirmationEmail = secondContact.Email;
        volunteerController.phone = '1234567890';
        volunteerController.zipCode = zipCode.Zip_Code_Unique__c;
        volunteerController.eventCode = '123';

        test.startTest();

        PageReference getStartedThankYouPage = volunteerController.submit();
        if (getStartedThankYouPage != null) {
            Test.setCurrentPage(getStartedThankYouPage);
            VolunteerController.searchRoles();
        }

        Contact matchingContact = volunteerController.matchingContact;

        system.assertEquals(matchingContact.Id, oldContact.Id);
        system.assertNotEquals(getStartedThankYouPage, null);
        system.assertEquals(volunteerController.zipCode, matchingContact.MailingPostalCode);

        test.stopTest();
    }
    static testMethod void test_21_Scenario4_EmailAndNameExistOnContactAndLead() {
        String firstName = 'ContactLeadFN';
        String lastName = 'ContactLeadLN';
        String email = 'ContactLeadFN.ContactLeadLN@test.com';

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId = rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.FirstName = firstName;
        contact.LastName = lastName;
        contact.rC_Bios__Home_Email__c = email;
        insert contact;

        Lead lead = rC_GSATests.initializeLead(false);
        lead.FirstName = firstName;
        lead.LastName = lastName;
        lead.Email = email;
        insert lead;

        VolunteerController volunteerController = new VolunteerController();
        volunteerController.firstName = firstName;
        volunteerController.lastName = lastName;
        volunteerController.email = email;
        volunteerController.confirmationEmail = email;
        volunteerController.phone = '1234567890';
        volunteerController.zipCode = zipCode.Zip_Code_Unique__c;
        volunteerController.eventCode = '123';

        test.startTest();

        PageReference getStartedThankYouPage = volunteerController.submit();
        if (getStartedThankYouPage != null) {
            Test.setCurrentPage(getStartedThankYouPage);
            VolunteerController.searchRoles();
        }

        Lead matchingLead = [Select Id, FirstName, LastName, Name, IsConverted, Email, Company From Lead where Id = :lead.Id];
        Contact matchingContact = volunteerController.matchingContact;

        system.assertEquals(matchingLead.IsConverted, false);
        system.assertNotEquals(getStartedThankYouPage, null);
        system.assertEquals(volunteerController.zipCode, matchingContact.MailingPostalCode);

        test.stopTest();
    }

    static testMethod void test_21_Scenario5_EmailAndNameNotExistOnContactOrLead() {

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        VolunteerController volunteerController = new VolunteerController();
        volunteerController.firstName = 'No Match';
        volunteerController.lastName = 'Contact';
        volunteerController.email = 'test@nomatch.com';
        volunteerController.confirmationEmail = 'test@nomatch.com';
        volunteerController.phone = '1234567890';
        volunteerController.zipCode = zipCode.Zip_Code_Unique__c;
        volunteerController.eventCode = '123';

        Campaign TroopOrGroup = rC_GSATests.initializeCampaign('Troop Name', null, councilAccount.Id, volunteerController.zipCode, false);
        TroopOrGroup.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        TroopOrGroup.Event_Code__c = '123';
        insert TroopOrGroup;

        test.startTest();
        volunteerController.getItems();

        PageReference getStartedThankYouPage = volunteerController.submit();
        if (getStartedThankYouPage != null) {
            Test.setCurrentPage(getStartedThankYouPage);
            VolunteerController.searchRoles();
        }
        Contact contact = volunteerController.getMatchingContact(volunteerController.firstName, volunteerController.lastName, volunteerController.email);

        system.assertNotEquals(contact, null);
        system.assertNotEquals(getStartedThankYouPage, null);
        test.stopTest();
    }

    static testMethod void test_21_Scenario6_EmailAndNameExistOn2Leads() {

        String firstName = 'DoubleLeadFN';
        String lastName = 'DoubleLeadLN';
        String email = 'DoubleLeadFN.DoubleLeadLN@test.com';

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        Lead lead = rC_GSATests.initializeLead(false);
        lead.FirstName = firstName;
        lead.LastName = lastName;
        lead.Email = email;
        insert lead;

        Lead newLead = rC_GSATests.initializeLead(false);
        newLead.FirstName = firstName;
        newLead.LastName = lastName;
        newLead.Email = email;
        insert newLead;

        test.startTest();

        VolunteerController volunteerController = new VolunteerController();
        volunteerController.firstName = firstName;
        volunteerController.lastName = lastName;
        volunteerController.email = email;
        volunteerController.confirmationEmail = email;
        volunteerController.phone = '1234567890';
        volunteerController.zipCode = zipCode.Zip_Code_Unique__c;
        volunteerController.eventCode = '123';

        volunteerController.submit();
        Lead matchingLead1 = [Select Id, IsConverted from Lead where Id = :lead.Id];
        Lead matchingLead2 = [Select Id, IsConverted from Lead where Id = :newLead.Id];
        Contact contact = volunteerController.getMatchingContact(firstName, lastName, email);

        system.assertEquals(matchingLead1.IsConverted, true);
        system.assertEquals(matchingLead2.IsConverted, false);
        system.assertEquals(contact.FirstName, firstName);
        system.assertEquals(contact.LastName, lastName);
        test.stopTest();
    }

    static testMethod void test_21_Scenario8_DoNotSeeCouncilSpecificInformation() {

        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Account account = rC_GSATests.initializeAccount(false);
        account.RecordTypeId = accountCouncilRecordTypeId;
        insert account;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(account.Id, true);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId = rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert contact;

        Contact newContact = [select Id, FirstName, LastName, Email from Contact where Id = :contact.Id];

        VolunteerController volunteerController = new VolunteerController();
        volunteerController.firstName = contact.FirstName;
        volunteerController.lastName = contact.LastName;
        volunteerController.email = contact.Email;
        volunteerController.confirmationEmail = contact.Email;
        volunteerController.phone = '1234567890';
        volunteerController.zipCode = zipCode.Zip_Code_Unique__c;
        volunteerController.eventCode = '123';

        test.startTest();
        PageReference getStartedThankYouPage = volunteerController.submit();
        if (getStartedThankYouPage != null) {
            Test.setCurrentPage(getStartedThankYouPage);
        }
        String councilId = Apexpages.currentPage().getParameters().get('CouncilId');
        Contact matchingContact = volunteerController.getMatchingContact(volunteerController.firstName, volunteerController.lastName, volunteerController.email);

        system.assertNotEquals(matchingContact, null);
        test.stopTest();
    }

        static testMethod void test_21_redirectingToVolunteerFlowwithParentContact() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Account account = rC_GSATests.initializeAccount(false);
        account.RecordTypeId = accountCouncilRecordTypeId;
        insert account;

        Contact parentContact = rC_GSATests.initializeParentContact(account,false);
        parentContact.rC_Bios__Preferred_Contact__c = true;
        insert parentContact;

        Contact childContact = rC_GSATests.initializeContact(account,true);
        Campaign campaign = rC_GSATests.initializeCampaignNew(account.Id, '12345', true);
        CampaignMember campaignMember = new CampaignMember(ContactId = parentContact.Id, CampaignId= campaign.Id);
        insert campaignMember;
        Test.startTest();
        Pagereference girlDemoGraphThankYou = Page.Girl_DemographicsThankYou;
        girlDemoGraphThankYou.getParameters().put('GirlContactId', childContact.Id);
        girlDemoGraphThankYou.getParameters().put('ParentContactId',parentContact.Id);
        girlDemoGraphThankYou.getParameters().put('isBackgroundCheckFlag','true');
        girlDemoGraphThankYou.getParameters().put('CampaignMemberIds',campaignMember.Id);
        Test.setCurrentPage(girlDemoGraphThankYou);

        Girl_DemoThankyouPageController demoThankYouController = new Girl_DemoThankyouPageController();
        Pagereference pagereference = demoThankYouController.redirectToVolunteerRegistration();
        test.stopTest();
        system.assert(string.valueOf(pagereference).contains('ParentContactId'));
    }
        
            static testMethod void test_21_1915_ZipCodeOwnerAssignedToTimothyAy() {
        
        ID ProfileID = [ Select Id from Profile where Name = 'System Administrator'][0].id;
        User userToCreate = new User();
        userToCreate.FirstName = 'Timothy';
        userToCreate.LastName  = 'Ay';
        userToCreate.Email     = 'dvdkliu+sfdc99@gmail.com';
        userToCreate.Username  = 'sfdc123435-dreamer@gmail.com';
        userToCreate.Alias     = 'fatty';
        userToCreate.ProfileId = ProfileID;
        userToCreate.TimeZoneSidKey    = 'America/Denver';
        userToCreate.LocaleSidKey      = 'en_US';
        userToCreate.EmailEncodingKey  = 'UTF-8';
        userToCreate.LanguageLocaleKey = 'en_US';
        insert userToCreate;
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCode.OwnerId = userToCreate.Id;
        insert zipCode;
        
        VolunteerController volunteerController = new VolunteerController();
        volunteerController.zipCode = '11111';
        
        test.startTest();
            Pagereference volunteerBasicPage = Page.Volunteer_BasicMembershipInformation;
            Test.setCurrentPage(volunteerBasicPage);
            volunteerController.submit();
        test.stopTest();
        
        boolean b = false;
        List<ApexPages.Message> msgList = ApexPages.getMessages();
        for(ApexPages.Message msg :  ApexPages.getMessages()) {
            if(msg.getDetail().contains('This council has not been enabled for the new registration/renewal system.'))
            b = true;
        }
        system.assert(b);
    }
            
    static testMethod void test_31_1915_ZipCodeOwnerAssignedToTimothyAy() {
        ID ProfileID = [ Select Id from Profile where Name = 'System Administrator'][0].id;
        User userToCreate = new User();
        userToCreate.FirstName = 'Timothy';
        userToCreate.LastName  = 'Ay';
        userToCreate.Email     = 'dvdkliu+sfdc99@gmail.com';
        userToCreate.Username  = 'sfdc123435-dreamer@gmail.com';
        userToCreate.Alias     = 'fatty';
        userToCreate.ProfileId = ProfileID;
        userToCreate.TimeZoneSidKey    = 'America/Denver';
        userToCreate.LocaleSidKey      = 'en_US';
        userToCreate.EmailEncodingKey  = 'UTF-8';
        userToCreate.LanguageLocaleKey = 'en_US';
        insert userToCreate;
        
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCode.OwnerId = userToCreate.Id;
        insert zipCode;
        
        Girl_BasicMembershipInfoController girlController = new Girl_BasicMembershipInfoController();
        girlController.zipCode = '11111';
        
        test.startTest();
            Pagereference girlBasicPage = Page.Girl_BasicMembershipInformation;
            Test.setCurrentPage(girlBasicPage);
            girlController.submit();
        test.stopTest();
        
        boolean b = false;
        List<ApexPages.Message> msgList = ApexPages.getMessages();
        for(ApexPages.Message msg :  ApexPages.getMessages()) {
            if(msg.getDetail().contains('This council has not been enabled for the new registration/renewal system.'))
            b = true;
        }
        system.assert(b);
    }
    
    
        static testMethod void test_23_SearchForTroopRoleByZipCode() {
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

        system.debug('volunteerProjectCampaignList[0]==>'+volunteerProjectCampaignList[0]+'   volunteerProjectCampaignList[1]'+volunteerProjectCampaignList[1]);

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

        system.debug('volunteerJobsCampaignList.zipCode=>'+volunteerJobsCampaignList);

        Test.startTest();

        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        List<Campaign> lstCampaignToUpdate = new List<Campaign>();
        for(Campaign objCampaign : volunteerProjectCampaignList) {
            objCampaign.Display_on_Website__c = true;
            lstCampaignToUpdate.add(objCampaign);
        }
        if(lstCampaignToUpdate <> null && lstCampaignToUpdate.size()>0)
        update lstCampaignToUpdate;

        List<Campaign> lstCampaignToUpdateSecond = new List<Campaign>();
        for(Campaign objCampignSecond :volunteerJobsCampaignList){
            objCampignSecond.Display_on_Website__c = true;
            lstCampaignToUpdateSecond.add(objCampignSecond);
        }
        if(lstCampaignToUpdateSecond <> null && lstCampaignToUpdateSecond.size()>0)
        update lstCampaignToUpdateSecond;

        Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
        volunteerTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;
        system.debug('volunteerTroopGroupRoleSearch.zipCode=>'+volunteerTroopGroupRoleSearch.zipCode);

        volunteerTroopGroupRoleSearch.selectedRadius = '15';
        //volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        //volunteerTroopGroupRoleSearch.selectedPageSize = '1';
        //volunteerTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        //volunteerTroopGroupRoleSearch.nextButtonClick();
        //volunteerTroopGroupRoleSearch.previousButtonClick();
        system.debug('volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size()=>'+volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size());
        system.debug('volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size()=>'+volunteerTroopGroupRoleSearch.parentCampaignWrapperList);
        volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked = true;
        volunteerTroopGroupRoleSearch.addCampaignMember();

        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);
        system.assertNotEquals(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignDistance, volunteerTroopGroupRoleSearch.parentCampaignWrapperList[2].campaignDistance);

        Contact contactNew = [Select Id, Name from Contact Where Id = :contact.Id];
        Campaign campaignNew = [Select Id, Name from Campaign Where Id = :volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId];

        CampaignMember campaignMember = [Select Id, ContactId, CampaignId from CampaignMember where ContactId = :contactNew.Id and  CampaignId = :campaignNew.Id];
        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }




    static testMethod void test_23_SearchForTroopRoleByNameAndZipCode() {
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

        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        List<Campaign> lstCampaignToUpdate = new List<Campaign>();
        for(Campaign objCampaign : volunteerProjectCampaignList) {
            objCampaign.Display_on_Website__c = true;
            lstCampaignToUpdate.add(objCampaign);
        }
        if(lstCampaignToUpdate <> null && lstCampaignToUpdate.size()>0)
        update lstCampaignToUpdate;

        List<Campaign> lstCampaignToUpdateSecond = new List<Campaign>();
        for(Campaign objCampignSecond :volunteerJobsCampaignList){
            objCampignSecond.Display_on_Website__c = true;
            lstCampaignToUpdateSecond.add(objCampignSecond);
        }
        if(lstCampaignToUpdateSecond <> null && lstCampaignToUpdateSecond.size()>0)
        update lstCampaignToUpdateSecond;


        Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
        volunteerTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;

        volunteerTroopGroupRoleSearch.selectedRadius = '15';
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked = true;
        volunteerTroopGroupRoleSearch.addCampaignMember();

        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);
        system.assertNotEquals(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignDistance, volunteerTroopGroupRoleSearch.parentCampaignWrapperList[2].campaignDistance);

        Contact contactNew = [Select Id, Name from Contact Where Id = :contact.Id];
        Campaign campaignNew = [Select Id, Name from Campaign Where Id = :volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId];

        CampaignMember campaignMember = [Select Id, ContactId, CampaignId from CampaignMember where ContactId = :contactNew.Id and  CampaignId = :campaignNew.Id];
        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }





    static testMethod void test_23_SeeTroopNamesSameAsMySpelling() {
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

        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        List<Campaign> lstCampaignToUpdate = new List<Campaign>();
        for(Campaign objCampaign : volunteerProjectCampaignList) {
            objCampaign.Display_on_Website__c = true;
            lstCampaignToUpdate.add(objCampaign);
        }
        if(lstCampaignToUpdate <> null && lstCampaignToUpdate.size()>0)
        update lstCampaignToUpdate;

        List<Campaign> lstCampaignToUpdateSecond = new List<Campaign>();
        for(Campaign objCampignSecond :volunteerJobsCampaignList){
            objCampignSecond.Display_on_Website__c = true;
            lstCampaignToUpdateSecond.add(objCampignSecond);
        }
        if(lstCampaignToUpdateSecond <> null && lstCampaignToUpdateSecond.size()>0)
        update lstCampaignToUpdateSecond;

        Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.zipCode = '11111';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';

        List<String> jasonString = Volunteer_TroopGroupRoleSearchController.searchCampaingNames('Troop Le');

        system.assertNotEquals(jasonString.size(), 0);

        Test.stopTest();
    }





    static testMethod void test_23_SearchForTroopRoleByName() {
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
        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        troopOrGroupVolunteerProject.Display_on_Website__c = true;
        update troopOrGroupVolunteerProject;

        troopGroupVolunteerJob.Display_on_Website__c = true;
        update troopGroupVolunteerJob;

        Volunteer_TroopGroupRoleSearchController volTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
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

        for(Integer i = 0; i < volTroopGroupRoleSearch.parentCampaignWrapperList.size(); i++){
            if(volTroopGroupRoleSearch.parentCampaignWrapperList[i].childCampaignName != 'Unsure')
                volTroopGroupRoleSearch.parentCampaignWrapperList[i].isCampaignChecked = true;
        }

        volTroopGroupRoleSearch.addCampaignMember();

        system.assert(volTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        system.assertEquals(volTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked, true);

        Contact contactNew = [Select Id, Name from Contact Where Id = :contact.Id];
        Campaign campaignNew = [Select Id, Name from Campaign Where Id = :volTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId];

        CampaignMember campaignMember = [Select Id, ContactId, CampaignId from CampaignMember where ContactId = :contactNew.Id and  CampaignId = :campaignNew.Id];
        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }



 static testMethod void test_23_UnsureOfVolunteerRole() {
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

        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        List<Campaign> lstCampaignToUpdate = new List<Campaign>();
        for(Campaign objCampaign : volunteerJobsCampaignList) {
            objCampaign.Display_on_Website__c = true;
            lstCampaignToUpdate.add(objCampaign);
        }
        if(lstCampaignToUpdate <> null && lstCampaignToUpdate.size()>0)
        update lstCampaignToUpdate;
         List<Campaign> lstCampaignToUpdateSecond = new List<Campaign>();
        for(Campaign objCampignSecond :volunteerProjectCampaignList){
            objCampignSecond.Display_on_Website__c = true;
            lstCampaignToUpdateSecond.add(objCampignSecond);
        }
        if(lstCampaignToUpdateSecond <> null && lstCampaignToUpdateSecond.size()>0)
        update lstCampaignToUpdateSecond;

        Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
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

        Contact contactNew = [Select Id, Name from Contact Where Id = :contact.Id];
        Campaign campaignNew = [Select Id, Name from Campaign Where Id = :volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId];

        CampaignMember campaignMember = [Select Id, ContactId, CampaignId from CampaignMember where ContactId = :contactNew.Id and  CampaignId = :campaignNew.Id];
        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }


    static testmethod void test_23_SearchByNameWithTwoSameNamesExists(){
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

        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        List<Campaign> lstCampaignToUpdate = new List<Campaign>();
        for(Campaign objCampaign : volunteerJobsCampaignList) {
            objCampaign.Display_on_Website__c = true;
            lstCampaignToUpdate.add(objCampaign);
        }
        if(lstCampaignToUpdate <> null && lstCampaignToUpdate.size()>0)
        update lstCampaignToUpdate;

        Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
        volunteerTroopGroupRoleSearch.troopOrGroupName = 'Troop Ledader1';
        volunteerTroopGroupRoleSearch.selectedRadius = '5';

        volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        volunteerTroopGroupRoleSearch.selectedPageSize = '2';
        volunteerTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        volunteerTroopGroupRoleSearch.nextButtonClick();
        volunteerTroopGroupRoleSearch.previousButtonClick();

        for(Integer i = 0; i < volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size(); i++){
            if(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[i].childCampaignName != 'Unsure')
                volunteerTroopGroupRoleSearch.parentCampaignWrapperList[i].isCampaignChecked = true;
        }

        volunteerTroopGroupRoleSearch.addCampaignMember();

        system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        system.assertEquals(volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked, true);

        Contact contactNew = [Select Id, Name from Contact Where Id = :contact.Id];
        Campaign campaignNew = [Select Id, Name from Campaign Where Id = :volunteerTroopGroupRoleSearch.parentCampaignWrapperList[0].childCampaignId];

        CampaignMember campaignMember = [Select Id, ContactId, CampaignId from CampaignMember where ContactId = :contactNew.Id and  CampaignId = :campaignNew.Id];
        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }

    static testMethod void test_23_ClearSearch() {
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

        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
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

    static testMethod void test_23_TroopNotFoundSearchingByName() {
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

        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        try{
            Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
            volunteerTroopGroupRoleSearch.troopOrGroupName = 'Alex';
            volunteerTroopGroupRoleSearch.selectedRadius = '5';
            volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

            //system.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        }
        catch(Exception e){
            system.debug('$$$$$$$+++++++++++++++===>'+e);
        }

        Test.stopTest();
    }
    static testMethod void test_23_TroopNotFoundSearchingbyRadius() {
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

        Pagereference TroopGroupRoleSearchPage = Page.Volunteer_TroopOrGroupRoleSearch;//new Pagereference('/apex/Volunteer_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('ContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        try{
            Volunteer_TroopGroupRoleSearchController volunteerTroopGroupRoleSearch = new Volunteer_TroopGroupRoleSearchController();
            volunteerTroopGroupRoleSearch.zipCode = '12345';
            volunteerTroopGroupRoleSearch.selectedRadius = '5';
            volunteerTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

            //System.assert(volunteerTroopGroupRoleSearch.parentCampaignWrapperList.size() == 0);
        }
        catch(Exception e){
            system.debug('$$$$$$$+++++++++++++++===>'+e);
        }

        Test.stopTest();
    }


    static void test_24_redirectToPaymentPage() {
        rC_Giving__Opportunity_Setting__c opportunitySetting = rC_Giving__Opportunity_Setting__c.getInstance();
        opportunitySetting.rC_Giving__Disable_All__c = true;

        rC_Giving__Opportunity_Validation__c opportunityValidationSetting = rC_Giving__Opportunity_Validation__c.getInstance();
        //opportunityValidationSetting.rC_Giving__Disable_All__c = true;

        rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);

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
        insert contact;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, householdAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, householdAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        insert campaign;

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        insert campaignMember;

        String strCampaigns = string.valueOf(campaignMember.Id);

        test.startTest();

        Pagereference joinMemberShipPage = Page.Volunteer_JoinMembershipInformation;//new Pagereference('/apex/Volunteer_JoinMembershipInformation');
        joinMemberShipPage.getParameters().put('ContactId', contact.Id);
        joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
        joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
        Test.setCurrentPage(joinMemberShipPage);

        VolunteerJoinMembershipInfoController volunteerJoinMembershipInfoController = new VolunteerJoinMembershipInfoController();
        Date myDate = date.newinstance(1960, 2, 17);

        volunteerJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/2017');
        volunteerJoinMembershipInfoController.gender = 'male';
        volunteerJoinMembershipInfoController.preferredEmail = 'true';
        volunteerJoinMembershipInfoController.booleanTermsAndConditions = true;
        volunteerJoinMembershipInfoController.booleanContactPhotoOptIn = false;
        volunteerJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
        volunteerJoinMembershipInfoController.booleanContactEmailOptIn = false;
        volunteerJoinMembershipInfoController.booleanGrantRequested =false;
        volunteerJoinMembershipInfoController.mobilePhone = '2425435';
        volunteerJoinMembershipInfoController.preferredPhone = 'home';
        volunteerJoinMembershipInfoController.preferredEmail = 'home';

        volunteerJoinMembershipInfoController.streetLine1 = 'streetLine1';
        volunteerJoinMembershipInfoController.streetLine2 = 'streetLine2';
        volunteerJoinMembershipInfoController.city = 'Pune';
        volunteerJoinMembershipInfoController.zipCode = '12345';
        volunteerJoinMembershipInfoController.country = 'USA';
        volunteerJoinMembershipInfoController.state = 'CA';
        volunteerJoinMembershipInfoController.isLifetime = false;
        volunteerJoinMembershipInfoController.booleanOppMembershipOnPaper = false;

        volunteerJoinMembershipInfoController.getGenders();
        volunteerJoinMembershipInfoController.getPreferredEmails();
        volunteerJoinMembershipInfoController.getPreferredPhones();
        volunteerJoinMembershipInfoController.getmembershipProductList();

        List<PricebookEntry > PricebookEntryList = [
            Select Id
                 , Name
                 , Pricebook2Id
                 , Product2Id
                 , UnitPrice
                 , Pricebook2.Id
                 , Pricebook2.Name
                 , Pricebook2.IsActive
                 , Pricebook2.Description
              From PricebookEntry
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
        ];

        PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;
        volunteerJoinMembershipInfoController.membershipProduct = newpricebook.Id;

        Pagereference pageReference = volunteerJoinMembershipInfoController.submit();

        system.debug('== Pagereference =====:  ' + pageReference);

        Contact contactNew = [Select Id, Name, FirstName, LastName, Email, rC_Bios__Gender__c from Contact where Id = :contact.Id];

        system.assert(pageReference.getUrl().contains('Volunteer_PaymentProcessing'));
        system.assertEquals(volunteerJoinMembershipInfoController.firstName, contactNew.FirstName);
        system.assertEquals(volunteerJoinMembershipInfoController.lastName, contactNew.LastName);
        system.assertEquals(volunteerJoinMembershipInfoController.email, contactNew.Email);
        system.assertEquals(volunteerJoinMembershipInfoController.gender.toUpperCase(), contactNew.rC_Bios__Gender__c.toUpperCase());

        test.stopTest();
    }
    
    static testMethod void test_24_lifeTimeMembershipReregistration() {
        rC_Giving__Opportunity_Setting__c opportunitySetting = rC_Giving__Opportunity_Setting__c.getInstance();
        opportunitySetting.rC_Giving__Disable_All__c = true;

        rC_Giving__Opportunity_Validation__c opportunityValidationSetting = rC_Giving__Opportunity_Validation__c.getInstance();

        rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);

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
        insert contact;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        insert campaign;

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        insert campaignMember;
        
        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, parentCampaign, false);
        opportunity.Type = 'Lifetime Membership';
        opportunity.Contact__c = contact.id;
        opportunity.RecordTypeId = rC_GSATests.getOpportunityRecordTypeId('Membership');
        insert opportunity;
        String strCampaigns = string.valueOf(campaignMember.Id);

        test.startTest();

        Pagereference joinMemberShipPage = Page.Volunteer_JoinMembershipInformation;//new Pagereference('/apex/Volunteer_JoinMembershipInformation');
        joinMemberShipPage.getParameters().put('ContactId', contact.Id);
        joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
        joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
        Test.setCurrentPage(joinMemberShipPage);

        VolunteerJoinMembershipInfoController volunteerJoinMembershipInfoController = new VolunteerJoinMembershipInfoController();
        Date myDate = date.newinstance(1960, 2, 17);

        volunteerJoinMembershipInfoController.dateOfBirth = String.valueOf('12/1/2017');
        volunteerJoinMembershipInfoController.gender = 'male';
        volunteerJoinMembershipInfoController.preferredEmail = 'true';
        volunteerJoinMembershipInfoController.booleanTermsAndConditions = true;
        volunteerJoinMembershipInfoController.booleanContactPhotoOptIn = false;
        volunteerJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
        volunteerJoinMembershipInfoController.booleanContactEmailOptIn = false;
        volunteerJoinMembershipInfoController.booleanGrantRequested =false;
        volunteerJoinMembershipInfoController.mobilePhone = '2425435';
        volunteerJoinMembershipInfoController.preferredPhone = 'home';
        volunteerJoinMembershipInfoController.preferredEmail = 'home';
        volunteerJoinMembershipInfoController.isLifetime = true;  //lifetimeflag set to true

        volunteerJoinMembershipInfoController.streetLine1 = 'streetLine1';
        volunteerJoinMembershipInfoController.streetLine2 = 'streetLine2';
        volunteerJoinMembershipInfoController.city = 'Pune';
        volunteerJoinMembershipInfoController.zipCode = '12345';
        volunteerJoinMembershipInfoController.country = 'USA';
        volunteerJoinMembershipInfoController.state = 'CA';

        volunteerJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
        Pagereference pageReference = volunteerJoinMembershipInfoController.submit();
        CampaignMember campignMemberToFetch = [Select Id from CampaignMember where Membership__c =: opportunity.Id];
        system.assert(campignMemberToFetch <> null);
        test.stopTest();
    }

    @isTest(seeAllData = true)
    static void test_24_PayWithCashCheck() {

        rC_Giving__Opportunity_Setting__c opportunitySetting = rC_Giving__Opportunity_Setting__c.getInstance();
        opportunitySetting.rC_Giving__Disable_All__c = true;

        rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, true);

        //PublicSiteURL__c publicSiteURL = rC_GSATests.initializePublicSiteForVolunteer(true);
        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert householdAccount;

        Contact contact = rC_GSATests.initializeContact(householdAccount, false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.Birthdate = system.today().addYears(-15);
        insert contact;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        insert campaign;

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        insert campaignMember;

        String strCampaigns = string.valueOf(campaignMember.Id);

        test.startTest();

        Pagereference joinMemberShipPage = Page.Volunteer_JoinMembershipInformation;//new Pagereference('/apex/Volunteer_JoinMembershipInformation');
        joinMemberShipPage.getParameters().put('ContactId', contact.Id);
        joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
        joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
        Test.setCurrentPage(joinMemberShipPage);

        VolunteerJoinMembershipInfoController volunteerJoinMembershipInfoController = new VolunteerJoinMembershipInfoController();
        Date myDate = date.newinstance(1960, 2, 17);

        volunteerJoinMembershipInfoController.dateOfBirth = String.valueOf('10/02/2011');
        volunteerJoinMembershipInfoController.gender = 'male';
        volunteerJoinMembershipInfoController.preferredEmail = 'true';
        volunteerJoinMembershipInfoController.booleanTermsAndConditions = true;
        volunteerJoinMembershipInfoController.booleanContactPhotoOptIn = false;
        volunteerJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
        volunteerJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
        volunteerJoinMembershipInfoController.booleanContactEmailOptIn = false;
        volunteerJoinMembershipInfoController.mobilePhone = '2425435';
        volunteerJoinMembershipInfoController.preferredPhone = 'home';
        volunteerJoinMembershipInfoController.preferredEmail = 'home';
        volunteerJoinMembershipInfoController.booleanGrantRequested =false;
        volunteerJoinMembershipInfoController.booleanOppMembershipOnPaper = true;

        volunteerJoinMembershipInfoController.streetLine1 = 'streetLine1';
        volunteerJoinMembershipInfoController.streetLine2 = 'streetLine2';
        volunteerJoinMembershipInfoController.city = 'Pune';
        volunteerJoinMembershipInfoController.zipCode = '12345';
        volunteerJoinMembershipInfoController.country = 'USA';
        volunteerJoinMembershipInfoController.state = 'CA';

        volunteerJoinMembershipInfoController.getGenders();
        system.debug('--------.gender.toUpperCase()--------'+volunteerJoinMembershipInfoController.gender.toUpperCase());
        volunteerJoinMembershipInfoController.getPreferredEmails();
        volunteerJoinMembershipInfoController.getPreferredPhones();
        volunteerJoinMembershipInfoController.getmembershipProductList();

        List<PricebookEntry > PricebookEntryList = [
            Select Id
                 , Name
                 , Pricebook2Id
                 , Product2Id
                 , UnitPrice
                 , Pricebook2.Id
                 , Pricebook2.Name
                 , Pricebook2.IsActive
                 , Pricebook2.Description
              From PricebookEntry
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
        ];

        PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;
        volunteerJoinMembershipInfoController.membershipProduct = newpricebook.Id;

        Pagereference pageReference = volunteerJoinMembershipInfoController.submit();

        system.debug('== Pagereference =====:  ' + pageReference);

        Contact contactNew = [Select Id, Name, FirstName, LastName, Email, rC_Bios__Gender__c from Contact where Id = :contact.Id];

        if(pageReference != null && pageReference.getUrl() !=null && string.valueOf(pageReference.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pageReference.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                //system.assertEquals(pageName,'VOLUNTEER_THANKYOU');
        }
        system.assertEquals(volunteerJoinMembershipInfoController.firstName, contactNew.FirstName);
        system.assertEquals(volunteerJoinMembershipInfoController.lastName, contactNew.LastName);
        system.assertEquals(volunteerJoinMembershipInfoController.email, contactNew.Email);
        //system.assertEquals(volunteerJoinMembershipInfoController.gender.toUpperCase(), contactNew.rC_Bios__Gender__c.toUpperCase());

        test.stopTest();
    }

    @isTest(seeAllData = true)
    static void test_24_RequestFinancialAid() {

        rC_Giving__Opportunity_Setting__c opportunitySetting = rC_Giving__Opportunity_Setting__c.getInstance();
        opportunitySetting.rC_Giving__Disable_All__c = true;

        rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);

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
        insert contact;
        system.debug('--------contact--------'+contact);

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        insert campaign;

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        insert campaignMember;

        String strCampaigns = string.valueOf(campaignMember.Id);

        test.startTest();

        Pagereference joinMemberShipPage = Page.Volunteer_JoinMembershipInformation;//new Pagereference('/apex/Volunteer_JoinMembershipInformation');
        joinMemberShipPage.getParameters().put('ContactId', contact.Id);
        joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
        joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
        Test.setCurrentPage(joinMemberShipPage);

        VolunteerJoinMembershipInfoController volunteerJoinMembershipInfoController = new VolunteerJoinMembershipInfoController();
        Date myDate = date.newinstance(1960, 2, 17);

        volunteerJoinMembershipInfoController.dateOfBirth = String.valueOf('10/02/2011');
        volunteerJoinMembershipInfoController.gender = 'male';
        volunteerJoinMembershipInfoController.preferredEmail = 'true';
        volunteerJoinMembershipInfoController.booleanTermsAndConditions = true;
        volunteerJoinMembershipInfoController.booleanContactPhotoOptIn = false;
        volunteerJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
        volunteerJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
        volunteerJoinMembershipInfoController.booleanContactEmailOptIn = false;
        volunteerJoinMembershipInfoController.mobilePhone = '2425435';
        volunteerJoinMembershipInfoController.preferredPhone = 'home';
        volunteerJoinMembershipInfoController.preferredEmail = 'home';

        volunteerJoinMembershipInfoController.booleanGrantRequested = true;

        volunteerJoinMembershipInfoController.streetLine1 = 'streetLine1';
        volunteerJoinMembershipInfoController.streetLine2 = 'streetLine2';
        volunteerJoinMembershipInfoController.city = 'Pune';
        volunteerJoinMembershipInfoController.zipCode = '12345';
        volunteerJoinMembershipInfoController.country = 'USA';
        volunteerJoinMembershipInfoController.state = 'CA';

        volunteerJoinMembershipInfoController.getGenders();
        volunteerJoinMembershipInfoController.getPreferredEmails();
        volunteerJoinMembershipInfoController.getPreferredPhones();
        volunteerJoinMembershipInfoController.getmembershipProductList();

        List<PricebookEntry > PricebookEntryList = [
            Select Id
                 , Name
                 , Pricebook2Id
                 , Product2Id
                 , UnitPrice
                 , Pricebook2.Id
                 , Pricebook2.Name
                 , Pricebook2.IsActive
                 , Pricebook2.Description
              From PricebookEntry
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
        ];

        PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;
        volunteerJoinMembershipInfoController.membershipProduct = newpricebook.Id;

        Pagereference pageReference = volunteerJoinMembershipInfoController.submit();

        system.debug('== Pagereference =====:  ' + pageReference);

        Contact contactNew = [Select Id, Name, FirstName, LastName, Email, rC_Bios__Gender__c from Contact where Id = :contact.Id];

        if(pageReference != null && pageReference.getUrl() !=null && string.valueOf(pageReference.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pageReference.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                //system.assertEquals(pageName,'VOLUNTEER_THANKYOU');
        }
        system.assertEquals(volunteerJoinMembershipInfoController.firstName, contactNew.FirstName);
        system.assertEquals(volunteerJoinMembershipInfoController.lastName, contactNew.LastName);
        system.assertEquals(volunteerJoinMembershipInfoController.email, contactNew.Email);
        //system.assertEquals(volunteerJoinMembershipInfoController.gender.toUpperCase(), contactNew.rC_Bios__Gender__c.toUpperCase());

        test.stopTest();
    }

    @isTest(seeAllData = true)
    static void test_24_PayWithCredit() {

        rC_Giving__Opportunity_Setting__c opportunitySetting = rC_Giving__Opportunity_Setting__c.getInstance();
        opportunitySetting.rC_Giving__Disable_All__c = true;

        rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);

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
        insert contact;

        Campaign parentCampaign = rC_GSATests.initializeCampaign('Join Membership Test Parent Campaign', null, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Join Membership Test Campaign', parentCampaign.Id, councilAccount.Id, zipCode.Zip_Code_Unique__c, false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        insert campaign;

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= campaign.Id);
        insert campaignMember;

        String strCampaigns = string.valueOf(campaignMember.Id);

        test.startTest();

        Pagereference joinMemberShipPage = Page.Volunteer_JoinMembershipInformation;//new Pagereference('/apex/Volunteer_JoinMembershipInformation');
        joinMemberShipPage.getParameters().put('ContactId', contact.Id);
        joinMemberShipPage.getParameters().put('CouncilId', councilAccount.Id);
        joinMemberShipPage.getParameters().put('campaignMemberIds', strCampaigns);
        Test.setCurrentPage(joinMemberShipPage);

        VolunteerJoinMembershipInfoController volunteerJoinMembershipInfoController = new VolunteerJoinMembershipInfoController();
        Date myDate = date.newinstance(1960, 2, 17);
        volunteerJoinMembershipInfoController.getlistCountryItems();
        volunteerJoinMembershipInfoController.getlistStateItems();
        volunteerJoinMembershipInfoController.dateOfBirth = String.valueOf('10/02/2011');
        volunteerJoinMembershipInfoController.gender = 'male';
        volunteerJoinMembershipInfoController.preferredEmail = 'true';
        volunteerJoinMembershipInfoController.booleanTermsAndConditions = true;
        volunteerJoinMembershipInfoController.booleanContactPhotoOptIn = false;
        volunteerJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
        volunteerJoinMembershipInfoController.booleanContactTextPhoneOptIn = false;
        volunteerJoinMembershipInfoController.booleanContactEmailOptIn = false;
        volunteerJoinMembershipInfoController.mobilePhone = '2425435';
        volunteerJoinMembershipInfoController.preferredPhone = 'home';
        volunteerJoinMembershipInfoController.preferredEmail = 'home';

        volunteerJoinMembershipInfoController.booleanGrantRequested = false;

        volunteerJoinMembershipInfoController.streetLine1 = 'streetLine1';
        volunteerJoinMembershipInfoController.streetLine2 = 'streetLine2';
        volunteerJoinMembershipInfoController.city = 'Pune';
        volunteerJoinMembershipInfoController.zipCode = '12345';
        volunteerJoinMembershipInfoController.country = 'USA';
        volunteerJoinMembershipInfoController.state = 'CA';

        volunteerJoinMembershipInfoController.getGenders();
        volunteerJoinMembershipInfoController.getPreferredEmails();
        volunteerJoinMembershipInfoController.getPreferredPhones();
        volunteerJoinMembershipInfoController.getmembershipProductList();

        List<PricebookEntry > PricebookEntryList = [
            Select Id
                 , Name
                 , Pricebook2Id
                 , Product2Id
                 , UnitPrice
                 , Pricebook2.Id
                 , Pricebook2.Name
                 , Pricebook2.IsActive
                 , Pricebook2.Description
              From PricebookEntry
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
        ];

        PricebookEntry newpricebook = (PricebookEntryList != null && PricebookEntryList.size() > 0) ?  PricebookEntryList[0] : null ;
        volunteerJoinMembershipInfoController.membershipProduct = newpricebook.Id;

        Pagereference pageReference = volunteerJoinMembershipInfoController.submit();

        system.debug('== Pagereference =====:  ' + pageReference);

        Contact contactNew = [Select Id, Name, FirstName, LastName, Email, rC_Bios__Gender__c from Contact where Id = :contact.Id];

        if(pageReference != null && pageReference.getUrl() != null)
            system.assertequals(string.valueOf(pageReference.getUrl()).contains('Volunteer_ThankYou'),false);
        system.assertEquals(volunteerJoinMembershipInfoController.firstName, contactNew.FirstName);
        system.assertEquals(volunteerJoinMembershipInfoController.lastName, contactNew.LastName);
        system.assertEquals(volunteerJoinMembershipInfoController.email, contactNew.Email);
        //system.assertEquals(volunteerJoinMembershipInfoController.gender.toUpperCase(), contactNew.rC_Bios__Gender__c.toUpperCase());

        test.stopTest();
    }



    //@isTest(SeeAllData=true)
    static testmethod void test_25_PayWithCreditCard(){

        Account account = rC_GSATests.initializeAccount(false);
        account.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert account;

        Contact contact = rC_GSATests.initializeContact(account, true);
        contact.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);

        Campaign campaign = rC_GSATests.initializeCampaignNew(account.Id, '12345', true);

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id,campaign.Id,true);

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account, campaign, true);

        Pricebook2 pricebook2 = rC_GSATests.initializePricebook2( true);
        //Pricebook2 standardPriceBook = [select id from Pricebook2 where isStandard = true limit 1];
        
        Product2 product2 = rC_GSATests.initializeProduct2( true);
        
        Id standardPriceBookId = Test.getStandardPricebookId();
        system.debug('== standardPriceBookId ===:  ' + standardPriceBookId);

        //Create inventory
        rC_Inventory__Inventory__c inventory = new rC_Inventory__Inventory__c();
        inventory.rC_Inventory__Account__c = account.Id;
        inventory.rC_Inventory__Product__c = product2.Id;
        inventory.rC_Inventory__Quantity__c = 5;
        insert inventory;
        system.debug('=== inventory ===:  ' + inventory);

        PricebookEntry standardPricebookEntry = rC_GSATests.initializeStdPricebookEntry(standardPriceBookId, product2, true);
        OpportunityLineItem opportunityLineItem =  rC_GSATests.initializeOpportunityLineItem(standardPricebookEntry, opportunity, true);

        test.startTest();
        Pagereference VolunteerPaymentProcessingPage = Page.Volunteer_PaymentProcessing;
        VolunteerPaymentProcessingPage.getParameters().put('ContactId', contact.Id);
        VolunteerPaymentProcessingPage.getParameters().put('OpportunityId', opportunity.Id);
        VolunteerPaymentProcessingPage.getParameters().put('CampaignMemberIds', campaignMember.Id);
        VolunteerPaymentProcessingPage.getParameters().put('CouncilId', account.Id);
        test.setCurrentPage(VolunteerPaymentProcessingPage);
        
        rC_GSATests.insertGSAPaymentSetting();
        
        VolunteerPaymentProcessingController volunteerPaymentProcessingController = new VolunteerPaymentProcessingController();
        system.debug('--------volunteerPaymentProcessingController---------'+volunteerPaymentProcessingController);
        volunteerPaymentProcessingController.contactId = contact.Id;
        volunteerPaymentProcessingController.councilId = account.Id;
        volunteerPaymentProcessingController.opportunityId = opportunity.Id;
        volunteerPaymentProcessingController.campaignMemberIds = campaignMember.Id;

        volunteerPaymentProcessingController.firstName = 'firstName';
        volunteerPaymentProcessingController.lastName = 'lastName';
        volunteerPaymentProcessingController.address = 'testAddress';
        volunteerPaymentProcessingController.city = 'testCity';
        volunteerPaymentProcessingController.country = 'testCountry';
        volunteerPaymentProcessingController.state = 'testState';
        volunteerPaymentProcessingController.zipCode= 'testZipCode';
        volunteerPaymentProcessingController.cardHolderName= 'testCard';
        volunteerPaymentProcessingController.cardNumber= '4111111111111111';
        volunteerPaymentProcessingController.expMonth= '01';
        volunteerPaymentProcessingController.expYear= string.valueOf(system.today().year())+1;
        volunteerPaymentProcessingController.securityCode= '123';
        volunteerPaymentProcessingController.acceptGSPromiseAndLaw= true;
        volunteerPaymentProcessingController.amountValue= 15;

        volunteerPaymentProcessingController.getlistCountryItems();
        volunteerPaymentProcessingController.getlistexpMonth();
        volunteerPaymentProcessingController.getlistexpYear();
        volunteerPaymentProcessingController.getlistStateItems();

        PageReference paymentPageSelectRadio = volunteerPaymentProcessingController.createTransactionRecord();

        PageReference paymentPage = volunteerPaymentProcessingController.processMyOrder();

        test.stopTest();
    }



    static testMethod void rc_25_nextButtonLeadingToDemographicsInformationPage() {
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
        Opportunity opportunity = rC_GSATests.initializeOpportunity(accountCouncil, campaign, true);

        PageReference pageRef = Page.Volunteer_ThankYou;
        test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('ContactId',contact.Id);
            ApexPages.currentPage().getParameters().put('CouncilId',accountCouncil.Id);
            ApexPages.currentPage().getParameters().put('OpportunityId',opportunity.Id);
            ApexPages.currentPage().getParameters().put('CampaignMemberIds',campaignMember.Id);
            Apexpages.currentPage().getParameters().put('CashOrCheck','true');
            Apexpages.currentPage().getParameters().put('FinancialAidRequired','true');
        test.startTest();
        Volunteer_ThankYouPageController volunteerThankyouPageController = new Volunteer_ThankYouPageController();
        Pagereference pagerefernce = volunteerThankyouPageController.submit();
        test.stopTest();
        if(pagerefernce.getUrl() !=null && string.valueOf(pagerefernce.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pagerefernce.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEER_DEMOGRAPHICSINFORMATION');
        }
    }

    static testMethod void rc_28_isBackgroundCheckFlagIsTrue() {
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

        PageReference pageRef = Page.Volunteer_DemographicsThankyouPage;
        test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('ContactId',contact.Id);
            ApexPages.currentPage().getParameters().put('CouncilId',accountCouncil.Id);
            ApexPages.currentPage().getParameters().put('CampaignMemberIds',campaignMember.Id);
            Apexpages.currentPage().getParameters().put('isBackgroundCheckFlag','true');
        test.startTest();
        Volunteer_DemoThankyouPageController volunteerDemoThankyouPageController = new Volunteer_DemoThankyouPageController();
        Pagereference pagerefernce = volunteerDemoThankyouPageController.next();
        test.stopTest();
        if(pagerefernce.getUrl() !=null && string.valueOf(pagerefernce.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pagerefernce.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEER_WELCOMEPAGE');
        }
    }

    static testMethod void rc_28_isBackgroundCheckFlagIsFalse() {
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

        PageReference pageRef = Page.Volunteer_DemographicsThankyouPage;
        test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('ContactId',contact.Id);
            ApexPages.currentPage().getParameters().put('CouncilId',accountCouncil.Id);
            ApexPages.currentPage().getParameters().put('CampaignMemberIds',campaignMember.Id);
            Apexpages.currentPage().getParameters().put('isBackgroundCheckFlag','false');
        test.startTest();
        Volunteer_DemoThankyouPageController volunteerDemoThankyouPageController = new Volunteer_DemoThankyouPageController();
        Pagereference pagerefernce = volunteerDemoThankyouPageController.next();
        test.stopTest();
        if(pagerefernce.getUrl() !=null && string.valueOf(pagerefernce.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pagerefernce.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEER_WELCOMEPAGE');
        }
    }


static testmethod void rc_29_backGroundCheck() {
        Account account = rC_GSATests.initializeAccount(false);
        account.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert account;

        Account houseHoldAccount = rC_GSATests.initializeAccount(false);
        houseHoldAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert houseHoldAccount;

        Contact contact = rC_GSATests.initializeContact(houseHoldAccount, true);

        Campaign campaign = rC_GSATests.initializeCampaign('CampaignTest',null, account.Id, string.ValueOf(41101), false);
        campaign.Special_Handling__c  = true;
        campaign.Background_Check_Needed__c = true;
        insert campaign;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, true);

        PageReference pageRef = new Pagereference('apex/Volunteer_DemographicsInformation'); //Page.Volunteer_DemographicsInformation;
        test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('ContactId',contact.Id);
            ApexPages.currentPage().getParameters().put('CouncilId',account.Id);
            ApexPages.currentPage().getParameters().put('CampaignMemberIds',campaignMember.Id);
            ApexPages.currentPage().getParameters().put('OpportunityId',opportunity.Id);

        list<Selectoption> picklistEthnicityOption = new list<Selectoption>();
        picklistEthnicityOption.add(new Selectoption('I choose not to share', 'I choose not to share'));

        test.startTest();
        Volunteer_DemographicsInfoController volunteerDemographicsInfoController = new Volunteer_DemographicsInfoController();
        system.debug('***volunteerDemographicsInfoController****'+volunteerDemographicsInfoController.contactId);
        volunteerDemographicsInfoController.lstSelectedCampaignFields.addAll(picklistEthnicityOption);
        volunteerDemographicsInfoController.selectedEthnicity = 'I choose not to share';
        volunteerDemographicsInfoController.selectedRace = 'Asian';
        volunteerDemographicsInfoController.occupation = 'Engineer';
        volunteerDemographicsInfoController.adultNoOfYearsInGS = '12';
        volunteerDemographicsInfoController.girlNoOfYearsInGS = '5';
        volunteerDemographicsInfoController.getEthnicityOptionList();
        volunteerDemographicsInfoController.getRaceOptionList();
        Pagereference pagerefernce = volunteerDemographicsInfoController.submit();

        system.debug('pagerefernce###########'+pagerefernce);

        if(string.valueOf(pagerefernce) != null && string.valueOf(pagerefernce).contains('?')){
                string[] url = string.valueOf(pagerefernce).split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'VOLUNTEER_DEMOGRAPHICSTHANKYOUPAGE');
            }
        test.stopTest();
    }

    @isTest
    static void test_29_NeedingBackgroundCheckANDSpecialHandling(){
        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Contact contact = rC_GSATests.initializeContact(accountList[1], false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.MailingPostalCode = '19312';
        contact.rC_Bios__Home_Email__c ='testUserEMail+1234@gmail.com';
        insert contact;

        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '19312';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        insert zipCodeInstance1;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCodeInstance1.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = '';
        insert troopOrGroupVolunteerProject1;

        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', troopOrGroupVolunteerProject1.Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        troopGroupVolunteerJob1.Background_Check_Needed__c = true;
        troopGroupVolunteerJob1.Special_Handling__c = true;
        insert troopGroupVolunteerJob1;

        Campaign campaign = rC_GSATests.initializeCampaignNew(councilAccount.Id, '19312', true);

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= troopGroupVolunteerJob1.Id);
        insert campaignMember;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.StageName = 'Open';
        opportunity.AccountId = householdAccount.Id;
        opportunity.CampaignId = campaign.Id;
        insert opportunity;

        Background_Check__c backgroundCheck = new Background_Check__c(Contact__c = contact.Id, Background_Check_Status__c = 'Eligible');
        insert backgroundCheck;

        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Special_Handling_Status__c = 'Approved';
        update campaignMember;

        Profile profile = [Select p.Id From Profile p where Name = 'Partner Community Login Custom'];

        Member_Community_Profile__c memberCommunityProfile = new Member_Community_Profile__c();
        memberCommunityProfile.Name = 'Member Community Profile Id';
        memberCommunityProfile.ProfileId__c = profile.Id;
        insert memberCommunityProfile;

        test.startTest();
        opportunity.StageName = 'Completed';
        opportunity.Membership_Status__c = 'Welcome';
        opportunity.Background_Check__c = backgroundCheck.Id;
        opportunity.Type = 'Adult Membership';
        opportunity.Contact__c = contact.Id;
        update opportunity;

        User user = [Select ProfileId, ContactId, Name, LastName, Email, Alias, AccountId From User where Email = 'testUserEMail+1234@gmail.com'];

        system.assertEquals(user.Email, 'testuseremail+1234@gmail.com');
        system.assertEquals(user.Alias, 'TCo');
        system.assertEquals(user.ContactId, contact.Id);

        test.stopTest();
    }

    @isTest
    static void test_29_NeedingBackgroundCheckANDNotNeedingSpecialHandling(){
        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Contact contact = rC_GSATests.initializeContact(accountList[1], false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.MailingPostalCode = '19312';
        contact.rC_Bios__Home_Email__c ='LoginTest20+456@gmail.com';
        contact.firstName = 'LoginTest20';
        contact.lastName = 'LoginTest20';
        insert contact;

        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '19312';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        insert zipCodeInstance1;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCodeInstance1.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = '';
        insert troopOrGroupVolunteerProject1;

        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', troopOrGroupVolunteerProject1.Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        troopGroupVolunteerJob1.Background_Check_Needed__c = true;
        troopGroupVolunteerJob1.Special_Handling__c = false;
        insert troopGroupVolunteerJob1;

        Campaign campaign = rC_GSATests.initializeCampaignNew(councilAccount.Id, '19312', true);

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= troopGroupVolunteerJob1.Id);
        insert campaignMember;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.StageName = 'Open';
        opportunity.AccountId = householdAccount.Id;
        opportunity.CampaignId = campaign.Id;
        opportunity.Type = 'Adult Membership';
        insert opportunity;

        Background_Check__c backgroundCheck = new Background_Check__c(Contact__c = contact.Id, Background_Check_Status__c = 'Eligible');
        insert backgroundCheck;

        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Special_Handling_Status__c = 'Approved';
        update campaignMember;

        Profile profile = [Select p.Id From Profile p where Name = 'Partner Community Login Custom'];

        Member_Community_Profile__c memberCommunityProfile = new Member_Community_Profile__c();
        memberCommunityProfile.Name = 'Member Community Profile Id';
        memberCommunityProfile.ProfileId__c = profile.Id;
        insert memberCommunityProfile;

        test.startTest();
        opportunity.StageName = 'Completed';
        opportunity.Membership_Status__c = 'Welcome';
        opportunity.Background_Check__c = backgroundCheck.Id;
        opportunity.Contact__c = contact.Id;
        update opportunity;

        User user = [Select ProfileId, ContactId, Name, LastName, Email, Alias, AccountId From User where Email = 'LoginTest20+456@gmail.com'];

        system.assertEquals(user.Email, 'logintest20+456@gmail.com');
        system.assertEquals(user.Alias, 'LLo');
        system.assertEquals(user.ContactId, contact.Id);

        test.stopTest();
    }

    @isTest
    static void test_29_NeedingSpecialHandlingNotNeedingBackgroundCheck(){
        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Contact contact = rC_GSATests.initializeContact(accountList[1], false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.MailingPostalCode = '19312';
        contact.rC_Bios__Home_Email__c ='UserTest63+456@gmail.com';
        contact.firstName = 'UserTest63';
        contact.lastName = 'LoginTest63';
        insert contact;

        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '19312';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        insert zipCodeInstance1;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCodeInstance1.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = '';
        insert troopOrGroupVolunteerProject1;

        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', troopOrGroupVolunteerProject1.Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        troopGroupVolunteerJob1.Background_Check_Needed__c = false;
        troopGroupVolunteerJob1.Special_Handling__c = true;
        insert troopGroupVolunteerJob1;

        Campaign campaign = rC_GSATests.initializeCampaignNew(councilAccount.Id, '19312', true);

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= troopGroupVolunteerJob1.Id);
        insert campaignMember;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.StageName = 'Open';
        opportunity.AccountId = householdAccount.Id;
        opportunity.CampaignId = campaign.Id;
        insert opportunity;

        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Special_Handling_Status__c = 'Approved';
        update campaignMember;

        Profile profile = [Select p.Id From Profile p where Name = 'Partner Community Login Custom'];

        Member_Community_Profile__c memberCommunityProfile = new Member_Community_Profile__c();
        memberCommunityProfile.Name = 'Member Community Profile Id';
        memberCommunityProfile.ProfileId__c = profile.Id;
        insert memberCommunityProfile;

        test.startTest();
        opportunity.StageName = 'Completed';
        opportunity.Membership_Status__c = 'Welcome';
        opportunity.Type = 'Adult Membership';
        update opportunity;

        User user = [Select ProfileId, ContactId, Name, LastName, Email, Alias, AccountId From User where Email = 'UserTest63+456@gmail.com'];

        system.assertEquals(user.Email, 'usertest63+456@gmail.com');
        system.assertEquals(user.Alias, 'ULo');
        system.assertEquals(user.ContactId, contact.Id);

        test.stopTest();
    }

   @isTest
    static void  test_29_NeedingNeitherBackgroundCheckNorSpecialHandling(){
        List<Account> accountList = new List<Account>();

        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Contact contact = rC_GSATests.initializeContact(accountList[1], false);
        contact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        contact.MailingPostalCode = '19312';
        contact.rC_Bios__Home_Email__c ='BchkNotNeed+923@gmail.com';
        contact.firstName = 'BchkNotNeed';
        contact.lastName = 'BchkNotNeed';
        insert contact;

        Zip_Code__c zipCodeInstance1 = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCodeInstance1.Zip_Code_Unique__c = '19312';
        zipCodeInstance1.name = '19312';
        zipCodeInstance1.geo_location__Latitude__s =  40.03000;
        zipCodeInstance1.geo_location__Longitude__s = -75.44000;
        insert zipCodeInstance1;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, zipCodeInstance1.Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        troopOrGroupVolunteerProject1.Zip_Code__c = '';
        insert troopOrGroupVolunteerProject1;

        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', troopOrGroupVolunteerProject1.Id, councilAccount.Id, null, false);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        troopGroupVolunteerJob1.Background_Check_Needed__c = false;
        troopGroupVolunteerJob1.Special_Handling__c = false;
        insert troopGroupVolunteerJob1;

        Campaign campaign = rC_GSATests.initializeCampaignNew(councilAccount.Id, '19312', true);

        CampaignMember campaignMember = new CampaignMember(ContactId = contact.Id, CampaignId= troopGroupVolunteerJob1.Id);
        insert campaignMember;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(householdAccount, campaign, false);
        opportunity.StageName = 'Open';
        opportunity.AccountId = householdAccount.Id;
        opportunity.CampaignId = campaign.Id;
        insert opportunity;

        campaignMember.Membership__c = opportunity.Id;
        update campaignMember;

        Profile profile = [Select p.Id From Profile p where Name = 'Partner Community Login Custom'];

        Member_Community_Profile__c memberCommunityProfile = new Member_Community_Profile__c();
        memberCommunityProfile.Name = 'Member Community Profile Id';
        memberCommunityProfile.ProfileId__c = profile.Id;
        insert memberCommunityProfile;

        test.startTest();
        opportunity.StageName = 'Completed';
        opportunity.Membership_Status__c = 'Welcome';
        opportunity.Type = 'Adult Membership';
        update opportunity;

        User user = [Select ProfileId, ContactId, Name, LastName, Email, Alias, AccountId From User where Email = 'BchkNotNeed+923@gmail.com'];

        system.assertEquals(user.Email, 'bchknotneed+923@gmail.com');
        system.assertEquals(user.Alias, 'BBc');
        system.assertEquals(user.ContactId, contact.Id);

        test.stopTest();
    }

    /*@isTest (SeeAllData=true)
     static void rc_29_backGroundCheckNotNeededAndPortalUser() {
        Account account = rC_GSATests.initializeAccount(true);
        Contact contact = rC_GSATests.initializeParentContact(account, false);
        contact.FirstName = 'PortalContact';
        contact.LastName = 'User';
        insert contact;
        Campaign campaign = rC_GSATests.initializeCampaign('CampaignTestTemp',null, account.Id, string.ValueOf(41101), false);
        campaign.Special_Handling__c = false;
        campaign.Background_Check_Needed__c = false;
        insert campaign;
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, true);
        PageReference pageRef = Page.Volunteer_DemographicsInformation;
        test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('ContactId',contact.Id);
            ApexPages.currentPage().getParameters().put('CouncilId',account.Id);
            ApexPages.currentPage().getParameters().put('CampaignMemberIds',campaignMember.Id);

        test.startTest();
        Volunteer_DemographicsInfoController volunteerDemographicsInfoController = new Volunteer_DemographicsInfoController();
        volunteerDemographicsInfoController.selectedEthnicity = 'I choose not to share';
        volunteerDemographicsInfoController.selectedRace = 'Asian';
        volunteerDemographicsInfoController.submit();
        test.stopTest();
        List<User> userList =
            [Select Id
               From User
              Where username =:'parent@test.com'
             limit 1
            ];
        system.assert(userList.size() > 0);
    }*/

static testMethod void rc_30_contactAndMemberUpdateWithLinkInCouncilAccount() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();


        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        system.debug('***ProfileID***'+ ProfileID);
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        Account accountCouncil = rC_GSATests.initializeAccount(false);
        accountCouncil.Name = 'Dheeraj';
        accountCouncil.Welcome_Video_Link__c = 'https://www.salesforce.com';
        accountCouncil.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncil;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign1001',null,null,null,true);
        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
        contact.Welcome_Q1__c = 'Yes';
        contact.Welcome_Q2__c = 'Agree';
        contact.Welcome_Q3__c  ='Yes';
        contact.Welcome_Q4__c  = 'No';
        contact.Welcome_Q5__c = 'Yes';
        contact.MailingPostalCode = '11111';
        insert contact;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(accountCouncil.Id, true);

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

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Primary__c = false;
        campaignMember.Active__c = false;
        campaignMember.Date_Active__c = system.Today()-7;
        campaignMember.Welcome__c = true;
        campaignMember.Membership__c = opportunity.Id;
        insert campaignMember;

        test.startTest();
        system.runAs(portalUser){
        VolunteerWelcomePageController objVolunteerWelcomePageController = new VolunteerWelcomePageController();
        //Data From UI
        objVolunteerWelcomePageController.selectVideoValue = 'Yes';
        objVolunteerWelcomePageController.selectGirlScoutValue = 'Agree';
        objVolunteerWelcomePageController.selectVolunteerValue = 'No';
        objVolunteerWelcomePageController.selectInformationValue = 'Yes';
        objVolunteerWelcomePageController.selectStaffValue = 'Yes';
        objVolunteerWelcomePageController.submit();
        test.stopTest();

        List<Contact> lstContact = rC_GSATests.getContactList(contact);
        system.assertEquals(1, lstContact.size());
        system.assertEquals(lstContact[0].Welcome_Complete__c, true);

        List<Opportunity> opportunityList = rC_GSATests.getOpportunityList(opportunity);
        system.assertEquals(1, opportunityList.size());
        system.assert(opportunityList[0].Membership_Status__c == 'Active');
        }
    }

    @isTest(SeeAllData=true)
   static void rc_30_contactAndMemberUpdateWithLinkNotPresentInCouncilAccount() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();


        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        system.debug('***ProfileID***'+ ProfileID);
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        Account accountCouncil = rC_GSATests.initializeAccount(false);
        accountCouncil.Name = 'Dheeraj';
        accountCouncil.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncil;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign1001',null,null,null,true);
        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
        contact.Welcome_Q1__c = 'Yes';
        contact.Welcome_Q2__c = 'Agree';
        contact.Welcome_Q3__c  ='Yes';
        contact.Welcome_Q4__c  = 'Yes';
        contact.Welcome_Q5__c = 'Yes';
        contact.MailingPostalCode = '11111';
        insert contact;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(accountCouncil.Id, true);

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

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Primary__c = false;
        campaignMember.Active__c = false;
        campaignMember.Date_Active__c = system.Today()-7;
        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Welcome__c = true;
        insert campaignMember;

        test.startTest();
        system.runAs(portalUser){
        VolunteerWelcomePageController objVolunteerWelcomePageController = new VolunteerWelcomePageController();
        objVolunteerWelcomePageController.homePage();
        objVolunteerWelcomePageController.init();
        //Data From UI
        objVolunteerWelcomePageController.selectVideoValue = 'Yes';
        objVolunteerWelcomePageController.selectGirlScoutValue = 'Agree';
        objVolunteerWelcomePageController.selectVolunteerValue = 'Yes';
        objVolunteerWelcomePageController.selectInformationValue = 'Yes';
        objVolunteerWelcomePageController.selectStaffValue = 'Yes';

        objVolunteerWelcomePageController.submit();

        test.stopTest();


 /*     The following asserts were commented out on 2014-08-14 by philip.nelson@roundcorner.com as part of the initial go-live process.
        Please contact sharif.shaalan@roundcorner.com or tim.ay@roundcorner.com with any questions.

        List<Contact> lstContact = rC_GSATests.getContactList(contact);
        system.assertEquals(1, lstContact.size());
        system.assertEquals(lstContact[0].Welcome_Complete__c, true);

        List<Opportunity> opportunityList = rC_GSATests.getOpportunityList(opportunity);
        system.assertEquals(1, opportunityList.size());
        system.assert(opportunityList[0].Membership_Status__c == 'Active');
  */
        }
    }

    static testMethod void test_30_girlRegistrationHeader() {
        Account accountCouncil = rC_GSATests.initializeAccount(false);
        accountCouncil.name = 'CouncilAccount';
        accountCouncil.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountCouncil.Council_Header__c = 'testCouncil';
        insert accountCouncil;

        test.startTest();
        Pagereference TroopGroupRoleSearchPage = Page.Girl_TroopOrGroupRoleSearch;//new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', accountCouncil.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
            Girl_RegistrationHeaderController girlRegistrationHeaderController = new Girl_RegistrationHeaderController();
        test.stopTest();
}
    /////////////////////////////////////////////////////////////////////////
    ///// GSA-31 Test Methods : Start
    //Parent and Contact found on Same Account
    static testMethod void test_31_parentAndChildContactFoundOnSameAccount() {
        Account accountHousehold = rC_GSATests.initializeAccount(true);
        Account accountCouncil = rC_GSATests.initializeAccount(false);
        accountCouncil.name = 'CouncilAccount';
        accountCouncil.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert accountCouncil;

        Account accountSchool = rC_GSATests.initializeAccount(false);
        accountSchool.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.SCHOOL_RECORDTYPE);
        insert accountSchool;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchool.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Contact contactGirl = rC_GSATests.initializeContact(accountHousehold,true);
        Contact contactParent = rC_GSATests.initializeParentContact(accountHousehold,true);
        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(accountCouncil.Id,true);

        test.startTest();

            ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
            Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);

            girlBasicMembershipController.firstName = 'Test';
            girlBasicMembershipController.lastName = 'Contact';
            girlBasicMembershipController.parentFirstName = 'Parent';
            girlBasicMembershipController.parentSecondName = 'Contact';
            girlBasicMembershipController.parentEmail = 'parent@test.com';
            girlBasicMembershipController.zipCode = '11111';
            girlBasicMembershipController.gradeFallValue = '1';
            girlBasicMembershipController.schoolAttendedId = accountSchool.Id;
            girlBasicMembershipController.eventCode = '123';
            girlBasicMembershipController.submit();

            List<Contact> contactList = [
                Select Id
                     , Grade__c
                     , FirstName
                     , MailingPostalCode
                  From Contact
                 Where Contact.FirstName = 'Parent' limit 1];
                 system.debug('contactList------------->'+contactList);
            system.assert(contactList.size() > 0 );

        test.stopTest();
    }
    //When no parent contact and child contact are found on Contact and Lead
    static testMethod void test_31_parentNotFoundAndChildContactNotFound() {
        PublicSiteURL__c publicSiteURL = new PublicSiteURL__c();
        publicSiteURL.Site_URL__c = 'http://gsdev-volunteersandbox.cs18.force.com/girl';
        publicSiteURL.Name = 'Girl_Registration';
        publicSiteURL.Volunteer_BaseURL__c = 'http://gsdev-volunteersandbox.cs18.force.com/girl';
        insert publicSiteURL;

        Account accountHousehold = rC_GSATests.initializeAccount(true);
        Account accountCouncil = rC_GSATests.initializeAccount(false);
        accountCouncil.name = 'CouncilAccount';
        accountCouncil.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert accountCouncil;

        Account accountSchool = rC_GSATests.initializeAccount(false);
        accountSchool.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.SCHOOL_RECORDTYPE);
        insert accountSchool;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchool.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(accountCouncil.Id,true);

        test.startTest();
            ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
            Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);
            girlBasicMembershipController.getGradeFallItems();
            girlBasicMembershipController.getItems();

            girlBasicMembershipController.firstName = 'Test';
            girlBasicMembershipController.lastName = 'Contact';
            girlBasicMembershipController.parentFirstName = 'Parent';
            girlBasicMembershipController.parentSecondName = 'Contact';
            girlBasicMembershipController.parentEmail = 'parent@test.com';
            girlBasicMembershipController.zipCode = '11111';
            girlBasicMembershipController.gradeFallValue = '1';
            girlBasicMembershipController.schoolAttendedId = accountSchool.Id;
            girlBasicMembershipController.eventCode = '123';

            girlBasicMembershipController.submit();

            List<Contact> contactList = [
                Select Id
                    , Grade__c
                    , MailingPostalCode
                From Contact
                Where Name = 'Parent Contact' limit 1];
            system.assertEquals(contactList[0].Grade__c,'1');
        test.stopTest();
    }

    //when Parent is found in Lead
    @isTest
    static void test_31_leadParentFound() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountSchoolRecordTypeId = aacountRecordTypeInfo.get('School').getRecordTypeId();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();

        Account accountHousehold = rC_GSATests.initializeAccount(true);
        List<Account> accountList = new List<Account>();

        Account accountCouncil = rC_GSATests.initializeAccount(false);
        accountCouncil.name = 'CouncilAccount';
        accountCouncil.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(accountCouncil);

        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.SCHOOL_RECORDTYPE);
        accountList.add(accountSchoolRecordType);
        insert accountList;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(accountCouncil.Id,true);
        Lead lead = rC_GSATests.initializeLead(false);
        lead.FirstName = 'LeadTester198';
        lead.LastName = 'LeadTester198';
        lead.Email = 'LeadTester198@gmail.com';
        insert lead;

    system.debug('lead##############  ' + lead);
        test.startTest();

        Pagereference girlBasic = Page.Girl_BasicMembershipInformation;
        Test.setCurrentPage(girlBasic);

        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);

        // Data From UI
        girlBasicMembershipController.firstName = 'Test1';
        girlBasicMembershipController.lastName = 'Contact1';
        girlBasicMembershipController.parentFirstName = lead.FirstName;
        girlBasicMembershipController.parentSecondName = lead.LastName;
        girlBasicMembershipController.parentEmail = lead.Email;
        girlBasicMembershipController.zipCode = '11111';
        girlBasicMembershipController.eventCode = '123';

        //Now ready with data to Submit
        girlBasicMembershipController.submit();

        List<Contact> contactList = [
            Select Id
              From Contact
             Where FirstName = :lead.FirstName
               and LastName = :lead.LastName
               and Email = :lead.Email
             limit 1
        ];
        system.debug('contactList ===:  ' + contactList);
        Lead convertedLead = [Select Id, FirstName, LastName, Email, isConverted From Lead where Id = :lead.Id];
        system.debug('convertedLead ===:  ' + convertedLead);

        system.assert(contactList.size() > 0);
        system.assertEquals(convertedLead.IsConverted, true);
        test.stopTest();
    }

   //When  child contact is found in other Account and Parent Contact is found in other Account
    static testMethod void test_31_parentContactFoundAndChildContactFoundOnOtherAccount () {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountSchoolRecordTypeId = aacountRecordTypeInfo.get('School').getRecordTypeId();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Id accountHouseholdRecordTypeId = aacountRecordTypeInfo.get('Household').getRecordTypeId();

        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = accountSchoolRecordTypeId;
        insert accountSchoolRecordType;

        Account accountHousehold = rC_GSATests.initializeAccount(false);
        accountHousehold.RecordTypeId = accountHouseholdRecordTypeId;
        insert accountHousehold;

        Account secondAccountHousehold = rC_GSATests.initializeAccount(false);
        secondAccountHousehold.RecordTypeId = accountHouseholdRecordTypeId;
        secondAccountHousehold.Name = 'New Household';
        insert secondAccountHousehold;

        Account accountCouncil = rC_GSATests.initializeAccount(false);
        accountCouncil.name = 'CouncilAccount';
        accountCouncil.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncil;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Contact contactGirl = rC_GSATests.initializeContact(accountHousehold,false);
        contactGirl.AccountId = accountHousehold.Id;
        contactGirl.FirstName = 'Girl';
        contactGirl.LastName = 'Contact';
        contactGirl.rC_Bios__Home_Email__c = 'amol.sable@roundcorner.com';
        insert contactGirl;
        system.debug('***contactGirlTest****'+contactGirl);

        Contact contactParent = rC_GSATests.initializeParentContact(accountHousehold,false);
        contactParent.AccountId = secondAccountHousehold.Id;
        insert contactParent;

         Zip_Code__c zipCode = rC_GSATests.initializeZipCode(accountCouncil.Id,true);

        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);
        //Data from UI
        girlBasicMembershipController.firstName = 'Girl';
        girlBasicMembershipController.lastName = 'Contact';
        girlBasicMembershipController.parentFirstName = 'Parent';
        girlBasicMembershipController.parentSecondName = 'Contact';
        girlBasicMembershipController.parentEmail = 'amol.sable@roundcorner.com';
        girlBasicMembershipController.eventCode = '123';
        girlBasicMembershipController.zipCode = '11111';
        //Now ready with data to Submit
        test.startTest();

        girlBasicMembershipController.submit();
        test.stopTest();
        List<Contact> contactList  = [
            Select Id
                 , AccountId
              From Contact
             Where Name = 'Girl Contact' limit 1
        ];
        system.debug('***contactList***'+contactList);
        List<Contact> contactListParent =[
            Select Id
                 , AccountId
              From Contact
             Where Name = 'Parent Contact' limit 1];
        system.debug('***contactListParent***'+contactListParent);
        system.assert(contactList.size() > 0);
        system.assert(contactList[0].AccountId == contactParent.AccountId);
        system.assert(contactListParent.size() > 0);
    }
    //When Campaign is found according to event code
    static testMethod void test_31_campaignFound() {
        Account accountCouncilRecordType = rC_GSATests.initializeAccount(false);
        accountCouncilRecordType.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert accountCouncilRecordType;

        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.SCHOOL_RECORDTYPE);
        insert accountSchoolRecordType;

        Account accountHousehold = rC_GSATests.initializeAccount(false);
        accountHousehold.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert accountHousehold;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Contact contactGirl = rC_GSATests.initializeContact(accountHousehold,false);
        contactGirl.AccountId = accountHousehold.Id;
        contactGirl.FirstName = 'Girl11';
        contactGirl.LastName = 'Girl11';
        contactGirl.rC_Bios__Home_Email__c = 'parent11@test.com';
        insert contactGirl;

        Contact contactParent = rC_GSATests.initializeParentContact(accountHousehold,false);
        contactParent.AccountId = accountHousehold.Id;
        contactGirl.FirstName = 'parent11';
        contactGirl.LastName = 'parent11';
        contactGirl.rC_Bios__Home_Email__c = 'parent11@test.com';
        insert contactParent;

        Zip_Code__c zipcode = rC_GSATests.initializeZipCode(accountCouncilRecordType.Id,true);
        Campaign campaign = rC_GSATests.initializeCampaign('TestCampaign',null,accountCouncilRecordType.Id,'11111', false);
        campaign.Event_Code__c = '12345';
        insert campaign;

        test.startTest();
        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);
        //Data from UI
        girlBasicMembershipController.firstName = 'Girl11';
        girlBasicMembershipController.lastName = 'Girl11';
        girlBasicMembershipController.parentFirstName = 'parent11';
        girlBasicMembershipController.parentSecondName = 'parent11';
        girlBasicMembershipController.parentEmail = 'parent11@test.com';
        girlBasicMembershipController.zipCode = '11111';
        girlBasicMembershipController.eventCode = '12345';
        girlBasicMembershipController.submit();

        List<CampaignMember> campaignMemberList = [
            Select Id
              From CampaignMember
             Where campaignId =:    campaign.Id];
        system.assert(campaignMemberList.size() > 0);
        test.stopTest();
    }


    //When only child contact is found
    static testMethod void test_31_parentContactNotFoundAndChildContactFoundOnOtherAccount() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Id accountSchoolRecordTypeId = aacountRecordTypeInfo.get('School').getRecordTypeId();

        Account accountHousehold = rC_GSATests.initializeAccount(false);
        accountHousehold.name = 'TempAccount';
        insert accountHousehold;

        Contact contactGirl = rC_GSATests.initializeContact(accountHousehold,true);

        Account accountCouncilRecordType = rC_GSATests.initializeAccount(false);
        accountCouncilRecordType.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncilRecordType;

        Contact contactChild= rC_GSATests.initializeContact(accountHousehold,true);

        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = accountSchoolRecordTypeId;
        insert accountSchoolRecordType;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Zip_Code__c zipcode = rC_GSATests.initializeZipCode(accountCouncilRecordType.Id,true);
        Campaign campaign = rC_GSATests.initializeCampaign('TestCampaign',null,accountCouncilRecordType.Id,'11111', false);
        campaign.Event_Code__c = '12345';
        insert campaign;

        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);
        //Data from UI
        girlBasicMembershipController.firstName = 'Test';
        girlBasicMembershipController.lastName = 'Contact';
        girlBasicMembershipController.parentFirstName = 'Parent';
        girlBasicMembershipController.parentSecondName = 'Contact';
        girlBasicMembershipController.parentEmail = 'parent@test.com';
        girlBasicMembershipController.zipCode = '11111';
        girlBasicMembershipController.eventCode = '12345';
        //Ready to submit the data
        test.startTest();
        girlBasicMembershipController.submit();
        test.stopTest();
        List<Contact> contactList = [Select Id,MailingPostalCode
                                    From Contact
                                    Where Name = 'Parent Contact' limit 1];
        system.assert(contactList[0].MailingPostalCode == '11111');
    }

     //When Parent Contact is not present in Contact
    static testMethod void test_31_parentContactFoundAndChildContactNotFound() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Id accountSchoolRecordTypeId = aacountRecordTypeInfo.get('School').getRecordTypeId();

        Account accountHousehold = rC_GSATests.initializeAccount(false);
        accountHousehold.name = 'TempAccount';
        insert accountHousehold;

        Contact contactParent= rC_GSATests.initializeParentContact(accountHousehold,true);

        Account accountCouncilRecordType = rC_GSATests.initializeAccount(false);
        accountCouncilRecordType.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncilRecordType;

        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = accountSchoolRecordTypeId;
        insert accountSchoolRecordType;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Zip_Code__c zipcode = rC_GSATests.initializeZipCode(accountCouncilRecordType.Id,true);
        Campaign campaign = rC_GSATests.initializeCampaign('TestCampaign',null,accountCouncilRecordType.Id,'11111', false);
        campaign.Event_Code__c = '12345';
        insert campaign;

        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);
        //Data from UI
        girlBasicMembershipController.firstName = 'Test';
        girlBasicMembershipController.lastName = 'Contact';
        girlBasicMembershipController.parentFirstName = 'Parent';
        girlBasicMembershipController.parentSecondName = 'Contact';
        girlBasicMembershipController.parentEmail = 'amol.sable@roundcorner.com';
        girlBasicMembershipController.zipCode = '11111';
        girlBasicMembershipController.eventCode = '12345';
        //Ready to submit the data
        test.startTest();
        girlBasicMembershipController.submit();
        test.stopTest();
        List<Contact> contactList = [Select Id,
                                     MailingPostalCode
                                    From Contact
                                    Where Name = 'Test Contact' limit 1];
        system.assert(contactList.size() > 0);
        system.assert(contactList[0].MailingPostalCode == '11111');
    }


    //When two matching Parent are found in Contact
    static testMethod void test_31_twoParentContactFound() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Id accountSchoolRecordTypeId = aacountRecordTypeInfo.get('School').getRecordTypeId();

        Account accountHousehold = rC_GSATests.initializeAccount(false);
        accountHousehold.name = 'TempAccount';
        insert accountHousehold;

        Account accountCouncilRecordType = rC_GSATests.initializeAccount(false);
        accountCouncilRecordType.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncilRecordType;

        Account  secondAccountHousehold = rC_GSATests.initializeAccount(true);

        Contact oldParent = rC_GSATests.initializeParentContact(secondAccountHousehold,false);
        oldParent.rC_Bios__Home_Email__c = 'parent@test.com';
        insert oldParent;

        Contact newParent = rC_GSATests.initializeParentContact(secondAccountHousehold,false);
        newParent.rC_Bios__Home_Email__c = 'parent@test.com';
        insert newParent;

        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = accountSchoolRecordTypeId;
        insert accountSchoolRecordType;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Zip_Code__c zipcode = rC_GSATests.initializeZipCode(accountCouncilRecordType.Id,true);
        Campaign campaign = rC_GSATests.initializeCampaign('TestCampaign',null,accountCouncilRecordType.Id,'11111', false);
        campaign.Event_Code__c = '12345';
        insert campaign;

        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);
        //Data from UI
        girlBasicMembershipController.firstName = 'Test';
        girlBasicMembershipController.lastName = 'Contact';
        girlBasicMembershipController.parentFirstName = 'Parent';
        girlBasicMembershipController.parentSecondName = 'Contact';
        girlBasicMembershipController.parentEmail = 'parent@test.com';
        girlBasicMembershipController.zipCode = '11111';
        girlBasicMembershipController.eventCode = '12345';
        //Ready to submit the data
        test.startTest();
        girlBasicMembershipController.submit();
        test.stopTest();
        List<Contact> contactList = [
            Select Id
                 , MailingPostalCode
                 , CreatedDate
              From Contact
             Where Name = 'Parent Contact'
          order by createdDate
             limit 1];
        system.assert(contactList[0].Id == oldParent.Id);
        system.assert(contactList.size() > 0);
        system.assert(contactList[0].MailingPostalCode == '11111');
    }

    //When zip code is incorrect
    static testMethod void test_31_zipCodeNotFound() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Id accountSchoolRecordTypeId = aacountRecordTypeInfo.get('School').getRecordTypeId();

        Account accountCouncilRecordType = rC_GSATests.initializeAccount(false);
        accountCouncilRecordType.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncilRecordType;

        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = accountSchoolRecordTypeId;
        insert accountSchoolRecordType;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);

        Zip_Code__c zipcode = rC_GSATests.initializeZipCode(accountCouncilRecordType.Id,true);
        //Data from UI
        girlBasicMembershipController.firstName = 'Test';
        girlBasicMembershipController.lastName = 'Contact';
        girlBasicMembershipController.parentFirstName = 'Test';
        girlBasicMembershipController.parentSecondName = 'Lead';
        girlBasicMembershipController.parentEmail = 'test@test.com';
        girlBasicMembershipController.zipCode = '00000';
        girlBasicMembershipController.eventCode = '12345';
        //Ready to submit the data
        test.startTest();
        PageReference troopSearchPage = girlBasicMembershipController.submit();
        test.stopTest();
        boolean b = false;
        List<ApexPages.Message> msgList = ApexPages.getMessages();
        system.debug('msgList=======>'+msgList);
        for(ApexPages.Message msg :  ApexPages.getMessages()) {
            system.debug('msg=======>'+msg);
            if(msg.getDetail().contains('Please check and re enter zip code.  If this is a new zip code, please enter a nearby zip code.'))
            b = true;
        }
        system.debug('b=======>'+b);
        system.assert(b);
        system.assertEquals(troopSearchPage, null);
    }

    //When two Matching Parent Lead Found in Lead
    static testMethod void test_31_twoParentLeadFound() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Id accountSchoolRecordTypeId = aacountRecordTypeInfo.get('School').getRecordTypeId();

        Account accountHousehold = rC_GSATests.initializeAccount(false);
        accountHousehold.name = 'TempAccount';
        insert accountHousehold;

        Account accountCouncilRecordType = rC_GSATests.initializeAccount(false);
        accountCouncilRecordType.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncilRecordType;

        Account  secondAccountHousehold = rC_GSATests.initializeAccount(true);

        Lead oldlead = rC_GSATests.initializeLead(true);

        Lead newLead = rC_GSATests.initializeLead(true);

        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = accountSchoolRecordTypeId;
        insert accountSchoolRecordType;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Zip_Code__c zipcode = rC_GSATests.initializeZipCode(accountCouncilRecordType.Id,true);
        Campaign campaign = rC_GSATests.initializeCampaign('TestCampaign',null,accountCouncilRecordType.Id,'11111', false);
        campaign.Event_Code__c = '12345';
        insert campaign;

        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);
        //Data from UI
        girlBasicMembershipController.firstName = 'Test';
        girlBasicMembershipController.lastName = 'Contact';
        girlBasicMembershipController.parentFirstName = 'Test';
        girlBasicMembershipController.parentSecondName = 'Lead';
        girlBasicMembershipController.parentEmail = 'test@test.com';
        girlBasicMembershipController.zipCode = '11111';
        girlBasicMembershipController.eventCode = '12345';
        //Ready to submit the data
        test.startTest();
        girlBasicMembershipController.submit();
        test.stopTest();
        List<Contact> contactList = [
            Select Id
                 , MailingPostalCode
                 , CreatedDate
              From Contact
             Where Name = 'Test Lead'
          order by createdDate
             limit 1];
        system.assert(contactList.size() > 0);
        system.assert(contactList[0].MailingPostalCode == '11111');
    }
    //When matching contact and matching lead is found
    static testMethod void test_31_contactAndLeadFound() {
        Map<String, Schema.RecordTypeInfo> aacountRecordTypeInfo = Schema.SObjectType.Account.getRecordTypeInfosByName();
        Id accountCouncilRecordTypeId = aacountRecordTypeInfo.get('Council').getRecordTypeId();
        Id accountSchoolRecordTypeId = aacountRecordTypeInfo.get('School').getRecordTypeId();

        Account accountHousehold = rC_GSATests.initializeAccount(false);
        accountHousehold.name = 'TempAccount';
        insert accountHousehold;

        Account accountCouncilRecordType = rC_GSATests.initializeAccount(false);
        accountCouncilRecordType.RecordTypeId = accountCouncilRecordTypeId;
        insert accountCouncilRecordType;

        Account  secondAccountHousehold = rC_GSATests.initializeAccount(true);

        Contact contactGirl = rC_GSATests.initializeParentContact(accountHousehold,true);

        Lead lead = rC_GSATests.initializeLead(false);
        lead.FirstName = 'Parent';
        lead.LastName = 'Contact';
        lead.Email = 'parent@test.com';
        insert lead;
        Account accountSchoolRecordType = rC_GSATests.initializeAccount(false);
        accountSchoolRecordType.RecordTypeId = accountSchoolRecordTypeId;
        insert accountSchoolRecordType;

        Contact contactSchoolAttended = new Contact();
        contactSchoolAttended.School_Attending__c = accountSchoolRecordType.id;
        contactSchoolAttended.LastName = 'MainContact';
        insert contactSchoolAttended;

        Zip_Code__c zipcode = rC_GSATests.initializeZipCode(accountCouncilRecordType.Id,true);
        Campaign campaign = rC_GSATests.initializeCampaign('TestCampaign',null,accountCouncilRecordType.Id,'11111', false);
        campaign.Event_Code__c = '12345';
        insert campaign;

        ApexPages.StandardController standardController = new ApexPages.StandardController(contactSchoolAttended);
        Girl_BasicMembershipInfoController girlBasicMembershipController = new Girl_BasicMembershipInfoController(standardController);
        //Data from UI
        girlBasicMembershipController.firstName = 'Test';
        girlBasicMembershipController.lastName = 'Contact';
        girlBasicMembershipController.parentFirstName = 'Parent';
        girlBasicMembershipController.parentSecondName = 'Contact';
        girlBasicMembershipController.parentEmail = 'parent@test.com';
        girlBasicMembershipController.zipCode = '11111';
        girlBasicMembershipController.eventCode = '12345';
        //Ready to submit the data
        test.startTest();
        girlBasicMembershipController.submit();
        test.stopTest();
        List<Contact> contactList = [
            Select Id
                 , MailingPostalCode
                 , CreatedDate
              From Contact
             Where firstName = 'Parent'
             limit 1];
        system.assert(contactList.size() > 0);
    }

    static testMethod void test_38_SearchForTroopRoleByName() {
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

        Pagereference TroopGroupRoleSearchPage = new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', girlContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        Girl_TroopGroupRoleSearchController girlTroopGroupRoleSearch = new Girl_TroopGroupRoleSearchController();
        girlTroopGroupRoleSearch.troopOrGroupName = 'Troop Group';
        girlTroopGroupRoleSearch.selectedRadius = '5';

        girlTroopGroupRoleSearch.selectedPageSize = '1';
        girlTroopGroupRoleSearch.selectedPageNumber = '1';

        girlTroopGroupRoleSearch.displayResultsOnPageNumberSelection();
        girlTroopGroupRoleSearch.nextButtonClick();
        girlTroopGroupRoleSearch.previousButtonClick();

        girlTroopGroupRoleSearch.getItems();
        girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        for(Integer i = 0; i < girlTroopGroupRoleSearch.parentCampaignWrapperList.size(); i++){
            if(girlTroopGroupRoleSearch.parentCampaignWrapperList[i].childCampaignName != 'Unsure')
                girlTroopGroupRoleSearch.parentCampaignWrapperList[i].isCampaignChecked = true;
        }

        girlTroopGroupRoleSearch.addCampaignMember();

        system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        system.assertEquals(girlTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked, true);

        Contact contactNew = [
            Select Id
                 , Name
              from Contact
             Where Id = :girlContact.Id
        ];
        Campaign campaignNew = [
            Select Id
                 , Name
              from Campaign
             Where Id = :girlTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignId
        ];

        CampaignMember campaignMember = [
            Select Id
                 , ContactId
                 , CampaignId
              from CampaignMember
             where ContactId = :contactNew.Id
               and  CampaignId = :campaignNew.Id
        ];

        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }


    static testMethod void test_38_SearchForTroopRoleByZipCode() {
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

        Pagereference TroopGroupRoleSearchPage = new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', girlContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('ParentContactId', parentContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        Girl_TroopGroupRoleSearchController girlTroopGroupRoleSearch = new Girl_TroopGroupRoleSearchController();
        girlTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;

        girlTroopGroupRoleSearch.selectedRadius = '20';
        girlTroopGroupRoleSearch.Grade = '--None--';
        girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        girlTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked = true;
        girlTroopGroupRoleSearch.addCampaignMember();

        system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);
        system.assertNotEquals(girlTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignDistance, girlTroopGroupRoleSearch.parentCampaignWrapperList[1].campaignDistance);

        Contact contactNew = [Select Id, Name from Contact Where Id = :girlContact.Id];
        Campaign campaignNew = [Select Id, Name from Campaign Where Id = :girlTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignId];

        CampaignMember campaignMember = [Select Id, ContactId, CampaignId from CampaignMember where ContactId = :contactNew.Id and  CampaignId = :campaignNew.Id];
        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }

    static testMethod void test_38_ClearSearch() {
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

        Pagereference TroopGroupRoleSearchPage = new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        Girl_TroopGroupRoleSearchController girlTroopGroupRoleSearch = new Girl_TroopGroupRoleSearchController();
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


    static testMethod void test_38_SeeTroopNamesSameAsMySpelling() {
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

        Pagereference TroopGroupRoleSearchPage = new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        Girl_TroopGroupRoleSearchController girlTroopGroupRoleSearch = new Girl_TroopGroupRoleSearchController();
        girlTroopGroupRoleSearch.troopOrGroupName = 'TroopGroupVolProj';
        girlTroopGroupRoleSearch.zipCode = '11111';
        girlTroopGroupRoleSearch.selectedRadius = '5';

        List<String> jasonString = Girl_TroopGroupRoleSearchController.searchCampaingNames('Troop');
        system.assertNotEquals(jasonString.size(), 0);

        Test.stopTest();
    }

    static testMethod void test_38_TroopNotFoundSearchingByName() {
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

        Pagereference TroopGroupRoleSearchPage = new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', contact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);

        try{
            Girl_TroopGroupRoleSearchController girlTroopGroupRoleSearch = new Girl_TroopGroupRoleSearchController();
            girlTroopGroupRoleSearch.troopOrGroupName = 'Alexabc';
            girlTroopGroupRoleSearch.selectedRadius = '5';
            girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();
            system.debug('girlTroopGroupRoleSearch.parentCampaignWrapperList.size()==>'+girlTroopGroupRoleSearch.parentCampaignWrapperList.size());

            system.assertEquals(girlTroopGroupRoleSearch.parentCampaignWrapperList.size(), 1);
        }
        catch(Exception e){
            system.debug('Exception==>'+e);
        }
        Test.stopTest();
    }






    static testMethod void test_38_UnsureCampaignCheck() {
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

        Contact girlContact = rC_GSATests.initializeContact(householdAccount, false);
        girlContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert girlContact;

        Contact parentContact = rC_GSATests.initializeContact(householdAccount, false);
        parentContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert parentContact;

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

        Pagereference TroopGroupRoleSearchPage = Page.Girl_TroopOrGroupRoleSearch;//new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', girlContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        List<Campaign> lstCampaignToUpdate = new List<Campaign>();
        for(Campaign objCampaign : volunteerJobsCampaignList) {
            objCampaign.Display_on_Website__c = true;
            lstCampaignToUpdate.add(objCampaign);
        }
        if(lstCampaignToUpdate <> null && lstCampaignToUpdate.size()>0)
        update lstCampaignToUpdate;
         List<Campaign> lstCampaignToUpdateSecond = new List<Campaign>();
        for(Campaign objCampignSecond :volunteerProjectCampaignList){
            objCampignSecond.Display_on_Website__c = true;
            lstCampaignToUpdateSecond.add(objCampignSecond);
        }
        if(lstCampaignToUpdateSecond <> null && lstCampaignToUpdateSecond.size()>0)
        update lstCampaignToUpdateSecond;
        Girl_TroopGroupRoleSearchController girlTroopGroupRoleSearch = new Girl_TroopGroupRoleSearchController();
        girlTroopGroupRoleSearch.troopOrGroupName = 'TroopGroupVolProj';

        girlTroopGroupRoleSearch.getItems();
        girlTroopGroupRoleSearch.getPageSizeOptions();
        girlTroopGroupRoleSearch.getRadiusInMiles();

        girlTroopGroupRoleSearch.selectedRadius = '5';

        girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        for(Integer i = 0; i < girlTroopGroupRoleSearch.parentCampaignWrapperList.size(); i++){
            if(girlTroopGroupRoleSearch.parentCampaignWrapperList[i].childCampaignName.equalsIgnoreCase('Unsure'))
                girlTroopGroupRoleSearch.parentCampaignWrapperList[i].isCampaignChecked = true;
        }

        system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        system.assertEquals(girlTroopGroupRoleSearch.parentCampaignWrapperList[1].isCampaignChecked, true);

        girlTroopGroupRoleSearch.createCampaignMemberOnUnsureCheck();

        Contact contactNew = [
            Select Id
                 , Name
              from Contact
             Where Id = :girlContact.Id
        ];
        Campaign campaignNew = [
            Select Id
                 , Name
              from Campaign
             Where Id = :girlTroopGroupRoleSearch.parentCampaignWrapperList[1].campaignId
        ];

        CampaignMember campaignMember = [
            Select Id
                 , ContactId
                 , CampaignId
              from CampaignMember
             where ContactId = :contactNew.Id
               and  CampaignId = :campaignNew.Id
        ];

        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }

    static testMethod void test_38_SelectIndividuallyRegisteredMember() {
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

        Contact girlContact = rC_GSATests.initializeContact(householdAccount, false);
        girlContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert girlContact;

        Contact parentContact = rC_GSATests.initializeContact(householdAccount, false);
        parentContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        insert parentContact;

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

        Pagereference TroopGroupRoleSearchPage = Page.Girl_TroopOrGroupRoleSearch;//new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', girlContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
        List<Campaign> lstCampaignToUpdate = new List<Campaign>();
        for(Campaign objCampaign : volunteerJobsCampaignList) {
            objCampaign.Display_on_Website__c = true;
            lstCampaignToUpdate.add(objCampaign);
        }
        if(lstCampaignToUpdate <> null && lstCampaignToUpdate.size()>0)
        update lstCampaignToUpdate;
         List<Campaign> lstCampaignToUpdateSecond = new List<Campaign>();
        for(Campaign objCampignSecond :volunteerProjectCampaignList){
            objCampignSecond.Display_on_Website__c = true;
            lstCampaignToUpdateSecond.add(objCampignSecond);
        }
        if(lstCampaignToUpdateSecond <> null && lstCampaignToUpdateSecond.size()>0)
        update lstCampaignToUpdateSecond;
        Girl_TroopGroupRoleSearchController girlTroopGroupRoleSearch = new Girl_TroopGroupRoleSearchController();
        girlTroopGroupRoleSearch.troopOrGroupName = 'TroopGroupVolProj';

        girlTroopGroupRoleSearch.getItems();
        girlTroopGroupRoleSearch.getPageSizeOptions();
        girlTroopGroupRoleSearch.getRadiusInMiles();

        girlTroopGroupRoleSearch.selectedRadius = '5';

        girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        for(Integer i = 0; i < girlTroopGroupRoleSearch.parentCampaignWrapperList.size(); i++){
            if(girlTroopGroupRoleSearch.parentCampaignWrapperList[i].childCampaignName.equalsIgnoreCase('Individually Registered Member'))
                girlTroopGroupRoleSearch.parentCampaignWrapperList[i].isCampaignChecked = true;
        }

        system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() != 0);
        system.assertEquals(girlTroopGroupRoleSearch.parentCampaignWrapperList[2].isCampaignChecked, true);

        girlTroopGroupRoleSearch.createCampaignMemberOnUnsureCheck();

        Contact contactNew = [
            Select Id
                 , Name
              from Contact
             Where Id = :girlContact.Id
        ];
        Campaign campaignNew = [
            Select Id
                 , Name
              from Campaign
             Where Id = :girlTroopGroupRoleSearch.parentCampaignWrapperList[2].campaignId
        ];

        CampaignMember campaignMember = [
            Select Id
                 , ContactId
                 , CampaignId
              from CampaignMember
             where ContactId = :contactNew.Id
               and  CampaignId = :campaignNew.Id
        ];

        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }

    static testMethod void test_38_SearchForTroopRoleByNameZipAndGrade() {
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
        troopOrGroupVolunteerProject1.Grade__c = '3';
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject1);

        Campaign troopOrGroupVolunteerProject2 = rC_GSATests.initializeCampaign('TroopGroupVolProj2',null, councilAccount.Id, zipCodeList[2].Zip_Code_Unique__c, false);
        troopOrGroupVolunteerProject2.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject2.Display_on_Website__c = true;
        troopOrGroupVolunteerProject2.Zip_Code__c = zipCodeList[2].Zip_Code_Unique__c;
        troopOrGroupVolunteerProject1.Grade__c = '3';
        volunteerProjectCampaignList.add(troopOrGroupVolunteerProject2);
        insert volunteerProjectCampaignList;

        Test.startTest();

        Pagereference TroopGroupRoleSearchPage = Page.Girl_TroopOrGroupRoleSearch;//new Pagereference('/apex/Girl_TroopOrGroupRoleSearch');
        TroopGroupRoleSearchPage.getParameters().put('GirlContactId', girlContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('ParentContactId', parentContact.Id);
        TroopGroupRoleSearchPage.getParameters().put('CouncilId', councilAccount.Id);
        Test.setCurrentPage(TroopGroupRoleSearchPage);
         List<Campaign> lstCampaignToUpdateSecond = new List<Campaign>();
        for(Campaign objCampignSecond :volunteerProjectCampaignList){
            objCampignSecond.Display_on_Website__c = true;
            lstCampaignToUpdateSecond.add(objCampignSecond);
        }
        if(lstCampaignToUpdateSecond <> null && lstCampaignToUpdateSecond.size()>0)
        update lstCampaignToUpdateSecond;
        Girl_TroopGroupRoleSearchController girlTroopGroupRoleSearch = new Girl_TroopGroupRoleSearchController();
        girlTroopGroupRoleSearch.zipCode = zipCodeList[0].Zip_Code_Unique__c;
        girlTroopGroupRoleSearch.troopOrGroupName = 'TroopGroupVolProj1';
        girlTroopGroupRoleSearch.Grade = '3';

        girlTroopGroupRoleSearch.getPageSizeOptions();
        girlTroopGroupRoleSearch.getRadiusInMiles();

        girlTroopGroupRoleSearch.selectedRadius = '20';
        girlTroopGroupRoleSearch.Grade = '--None--';
        girlTroopGroupRoleSearch.searchTroopORGroupRoleByNameORZip();

        girlTroopGroupRoleSearch.parentCampaignWrapperList[0].isCampaignChecked = true;
        girlTroopGroupRoleSearch.addCampaignMember();

        system.assert(girlTroopGroupRoleSearch.parentCampaignWrapperList.size() > 0);

        Contact contactNew = [Select Id, Name from Contact Where Id = :girlContact.Id];
        Campaign campaignNew = [Select Id, Name from Campaign Where Id = :girlTroopGroupRoleSearch.parentCampaignWrapperList[0].campaignId];

        CampaignMember campaignMember = [Select Id, ContactId, CampaignId from CampaignMember where ContactId = :contactNew.Id and  CampaignId = :campaignNew.Id];
        system.assert(campaignMember.Id != null);

        Test.stopTest();
    }

   @isTest(SeeAllData=true)
    static void test_39_ProvideMembershipInformation(){
        ID ProfileID = [ Select Id from Profile where Name = 'System Administrator'][0].id;
        List<Account> accountList = new List<Account>();
        List<Contact> contactList = new List<Contact>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        test.startTest();
        Account councilAccount = rC_GSATests.initializeAccount(true);
        councilAccount.Name = 'Girl Scouts of Eastern Pennsylvania, Inc.';
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(true);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        upsert accountList;

        system.debug('&&& herehgjhg');
        Contact parentContact = rC_GSATests.initializeContact(householdAccount, true);
        parentContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        parentContact.Birthdate = System.today().addYears(-10);
        system.debug('&&& herehgjhgb');
        parentContact.AccountId = accountList[1].Id;
        system.debug('&&& herehgjhbvg');
        contactList.add(parentContact);

         User portalUser = new User( email='test-user@email.com',
                                    //contactid = parentContact.id,
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

        Contact childContact = rC_GSATests.initializeContact(householdAccount, true);
        childContact.Birthdate = System.today();
        //childContact.Text_Phone_Opt_In__c = true;
        childContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        childContact.AccountId = accountList[1].Id;
        contactList.add(childContact);
        upsert contactList ;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, '12345', true);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        upsert troopOrGroupVolunteerProject1;

        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', troopOrGroupVolunteerProject1.Id, councilAccount.Id, null, true);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        upsert troopGroupVolunteerJob1;

        CampaignMember campaignMember = new CampaignMember(ContactId = childContact.Id, CampaignId= troopGroupVolunteerJob1.Id);
        upsert campaignMember;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCode.Recruiter__c = portalUser.Id;
        insert zipCode;

        PageReference joinMembershipInformationPage = new PageReference('apex/Girl_JoinMembershipInformation');
        joinMembershipInformationPage.getParameters().put('ParentContactId', contactList[0].Id);
        joinMembershipInformationPage.getParameters().put('GirlContactId', contactList[1].Id);
        joinMembershipInformationPage.getParameters().put('CouncilId', accountList[0].Id);
        joinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMember.Id);
        test.setCurrentPage(joinMembershipInformationPage);

        Girl_JoinMembershipInfoController girlJoinMembershipInfoController = new Girl_JoinMembershipInfoController();
        
         List<PricebookEntry> PricebookEntryList = [Select Pricebook2.Description,
                                                          Pricebook2.IsActive,
                                                          Pricebook2.Name,
                                                          Pricebook2.Id,
                                                          Pricebook2Id,
                                                          Product2Id,
                                                          UnitPrice,
                                                          Name,
                                                          Id
                                                     From PricebookEntry
                                                    where Pricebook2.Name = 'Girl Scouts USA'
                                                     and Pricebook2.IsActive = true
        ];

        girlJoinMembershipInfoController.membershipProduct = (PricebookEntryList <> NULL && PricebookEntryList.size() > 0) ? PricebookEntryList[0].Id : NULL;

        //girlJoinMembershipInfoController.membershipProduct = 'Girl Membership (Valid 10/1/2013 - 9/30/2014)';
        //girlJoinMembershipInfoController.selectedCustodialInfo = 'Both Parents';
        girlJoinMembershipInfoController.primaryFirstName = 'PrimaryContact1';
        girlJoinMembershipInfoController.primaryLastName = 'PrimaryContact1';
        girlJoinMembershipInfoController.primaryEmail = 'PrimaryContact1@gmail.com';
        girlJoinMembershipInfoController.primaryPreferredEmail = 'Email';
        girlJoinMembershipInfoController.primaryGender = 'Female';
        girlJoinMembershipInfoController.primaryHomePhone = '2233445';
        girlJoinMembershipInfoController.primaryPreferredPhone = 'Home Phone';
        girlJoinMembershipInfoController.primaryStreetLine1 = 'STreet1';
        girlJoinMembershipInfoController.primaryStreetLine2 = 'STreet1';
        girlJoinMembershipInfoController.primaryCity = 'city1';
        girlJoinMembershipInfoController.primaryState = 'State1';
        girlJoinMembershipInfoController.primaryZipCode ='11111';
        girlJoinMembershipInfoController.primaryCounty = '2';
        girlJoinMembershipInfoController.primaryCountry = 'COuntry';
        girlJoinMembershipInfoController.primaryEmailOptIn = true;

        girlJoinMembershipInfoController.secondaryFirstName = 'PrimaryContact1';
        girlJoinMembershipInfoController.secondaryLastName = 'PrimaryContact1';
        girlJoinMembershipInfoController.secondaryEmail = 'PrimaryContact1@gmail.com';
        girlJoinMembershipInfoController.secondaryGender = 'Female';
        girlJoinMembershipInfoController.secondaryPreferredEmail = 'Email';
        girlJoinMembershipInfoController.secondaryHomePhone = '2233445';
        girlJoinMembershipInfoController.secondaryPreferredPhone = 'Home Phone';
        girlJoinMembershipInfoController.secondaryStreetLine1 = 'STreet1';
        girlJoinMembershipInfoController.secondaryStreetLine2 = 'STreet1';
        girlJoinMembershipInfoController.secondaryCity = 'city1';
        girlJoinMembershipInfoController.secondaryState = 'State1';
        girlJoinMembershipInfoController.secondaryZipCode ='11111';
        girlJoinMembershipInfoController.secondaryCounty = '2';
        girlJoinMembershipInfoController.secondaryCountry = 'COuntry';
        girlJoinMembershipInfoController.secondaryEmailOptIn = true;

        girlJoinMembershipInfoController.MobilePhone = '989898243';
        girlJoinMembershipInfoController.primaryTextOptIn = true;
        girlJoinMembershipInfoController.dateOfBirth = '04/07/2014';
        girlJoinMembershipInfoController.custodialFlag = true;

        girlJoinMembershipInfoController.getPreferredEmails();
        girlJoinMembershipInfoController.getGenders();
        girlJoinMembershipInfoController.getPreferredPhones();
        system.debug('&&&& PricebookEntryList '+PricebookEntryList);
        girlJoinMembershipInfoController.getmembershipProductList();
        system.debug('&&&& PricebookEntryList '+PricebookEntryList);
        girlJoinMembershipInfoController.booleanTermsAndConditions = true;

        girlJoinMembershipInfoController.girlEmail = 'girlEmail@gmail.com';
        girlJoinMembershipInfoController.girlPhone = '23567';
        girlJoinMembershipInfoController.streetLine1 = 'STreet1';
        girlJoinMembershipInfoController.streetLine2 = 'STreet2';
        girlJoinMembershipInfoController.county = '2';
        girlJoinMembershipInfoController.country = 'country1';
        girlJoinMembershipInfoController.city = 'city1';
        girlJoinMembershipInfoController.state = 'State1';
        girlJoinMembershipInfoController.zipCode ='11111';
        girlJoinMembershipInfoController.secondaryTextOptIn = true;
        girlJoinMembershipInfoController.booleanGrantRequested = true;
        girlJoinMembershipInfoController.booleanOppMembershipOnPaper = true;
        PageReference pageReference = girlJoinMembershipInfoController.submit();
        /*
        if(pageReference.getUrl() !=null && string.valueOf(pageReference.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pageReference.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'GIRL_THANKYOU');
        }
       
        //system.assertequals(string.valueOf(pageReference.getUrl()).contains('Girl_ThankYou'),true);
        Account councilAcc = [Select Id, Name, Volunteer_Financial_Aid_Available__c from Account where Id = :councilAccount.Id];
        Contact childContact1 = [Select Id, Name, rC_Bios__Preferred_Email__c from Contact Where Id = :childContact.Id];
        Contact parentContact1 = [Select Id, Name, rC_Bios__Home_Email__c, HomePhone from Contact Where Id = :parentContact.Id];
        //system.assert(pageReference.getUrl().contains('Volunteer_PaymentProcessing'));
        //system.assertEquals(councilAcc.Volunteer_Financial_Aid_Available__c, true);
        system.assertEquals(childContact1.rC_Bios__Preferred_Email__c, 'Home');
        system.assertEquals(parentContact1.rC_Bios__Home_Email__c, 'primarycontact1@gmail.com');
        system.assertEquals(parentContact1.HomePhone, '2233445');
         */
        test.stopTest();
    }



    static void test_39_PayWithCash() {
        ID ProfileID = [ Select Id from Profile where Name = 'System Administrator'][0].id;
        List<Account> accountList = new List<Account>();
        List<Contact> contactList = new List<Contact>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        test.startTest();
        Account councilAccount = rC_GSATests.initializeAccount(true);
        councilAccount.Name = 'Girl Scouts of Eastern Pennsylvania, Inc.';
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(true);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        upsert accountList;

        User portalUser = new User( email='test-user@email.com',
                                    //contactid = parentContact.id,
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

        Contact parentContact = rC_GSATests.initializeContact(householdAccount, true);
        parentContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        parentContact.Birthdate = System.today();
        parentContact.AccountId = accountList[1].Id;
        contactList.add(parentContact);

        Contact childContact = rC_GSATests.initializeContact(householdAccount, true);
        childContact.Birthdate = System.today();
        childContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        childContact.AccountId = accountList[1].Id;
        contactList.add(childContact);
        upsert contactList ;

        Zip_Code__c zipCode = rC_GSATests.initializeZipCode(councilAccount.Id, false);
        zipCode.Recruiter__c = portalUser.Id;
        insert zipCode;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, '12345', true);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        upsert troopOrGroupVolunteerProject1;

        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', troopOrGroupVolunteerProject1.Id, councilAccount.Id, null, true);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        upsert troopGroupVolunteerJob1;

        CampaignMember campaignMember = new CampaignMember(ContactId = childContact.Id, CampaignId= troopGroupVolunteerJob1.Id);
        upsert campaignMember;

        PageReference joinMembershipInformationPage = new PageReference('apex/Girl_JoinMembershipInformation');
        joinMembershipInformationPage.getParameters().put('ParentContactId', contactList[0].Id);
        joinMembershipInformationPage.getParameters().put('GirlContactId', contactList[1].Id);
        joinMembershipInformationPage.getParameters().put('CouncilId', accountList[0].Id);
        joinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMember.Id);
        test.setCurrentPage(joinMembershipInformationPage);

        Girl_JoinMembershipInfoController girlJoinMembershipInfoController = new Girl_JoinMembershipInfoController();

        List<PricebookEntry> PricebookEntryList = [Select Pricebook2.Description,
                                                          Pricebook2.IsActive,
                                                          Pricebook2.Name,
                                                          Pricebook2.Id,
                                                          Pricebook2Id,
                                                          Product2Id,
                                                          UnitPrice,
                                                          Name,
                                                          Id
                                                     From PricebookEntry
                                                    where Pricebook2.Name = 'Girl Scouts USA'
                                                      and Pricebook2.IsActive = true
        ];

        girlJoinMembershipInfoController.membershipProduct = (PricebookEntryList <> NULL && PricebookEntryList.size() > 0) ? PricebookEntryList[0].Id : NULL;
        girlJoinMembershipInfoController.primaryFirstName = 'PrimaryContact1';
        girlJoinMembershipInfoController.primaryLastName = 'PrimaryContact1';
        girlJoinMembershipInfoController.primaryEmail = 'PrimaryContact1@gmail.com';
        girlJoinMembershipInfoController.primaryPreferredEmail = 'Email';
        girlJoinMembershipInfoController.primaryGender = 'Female';
        girlJoinMembershipInfoController.primaryHomePhone = '2233445';
        girlJoinMembershipInfoController.primaryPreferredPhone = 'Home Phone';
        girlJoinMembershipInfoController.primaryStreetLine1 = 'STreet1';
        girlJoinMembershipInfoController.primaryStreetLine2 = 'STreet1';
        girlJoinMembershipInfoController.primaryCity = 'city1';
        girlJoinMembershipInfoController.primaryState = 'State1';
        girlJoinMembershipInfoController.primaryZipCode ='354666';
        girlJoinMembershipInfoController.primaryCounty = '2';
        girlJoinMembershipInfoController.primaryCountry = 'COuntry';
        girlJoinMembershipInfoController.primaryEmailOptIn = true;

        girlJoinMembershipInfoController.secondaryFirstName = 'PrimaryContact1';
        girlJoinMembershipInfoController.secondaryLastName = 'PrimaryContact1';
        girlJoinMembershipInfoController.secondaryEmail = 'PrimaryContact1@gmail.com';
        girlJoinMembershipInfoController.secondaryGender = 'Female';
        girlJoinMembershipInfoController.secondaryPreferredEmail = 'Email';
        girlJoinMembershipInfoController.secondaryHomePhone = '2233445';
        girlJoinMembershipInfoController.secondaryPreferredPhone = 'Home Phone';
        girlJoinMembershipInfoController.secondaryStreetLine1 = 'STreet1';
        girlJoinMembershipInfoController.secondaryStreetLine2 = 'STreet1';
        girlJoinMembershipInfoController.secondaryCity = 'city1';
        girlJoinMembershipInfoController.secondaryState = 'State1';
        girlJoinMembershipInfoController.secondaryZipCode ='354666';
        girlJoinMembershipInfoController.secondaryCounty = '2';
        girlJoinMembershipInfoController.secondaryCountry = 'COuntry';
        girlJoinMembershipInfoController.secondaryEmailOptIn = true;

        girlJoinMembershipInfoController.MobilePhone = '989898243';
        girlJoinMembershipInfoController.primaryTextOptIn = true;
        girlJoinMembershipInfoController.dateOfBirth = '04/07/2014';
        girlJoinMembershipInfoController.custodialFlag = true;
        girlJoinMembershipInfoController.booleanTermsAndConditions = true;

        girlJoinMembershipInfoController.getcustodialCareInfo();
        girlJoinMembershipInfoController.getGenders();
        girlJoinMembershipInfoController.getPreferredPhones();
        girlJoinMembershipInfoController.getmembershipProductList();
        girlJoinMembershipInfoController.getlistStateItems();
        girlJoinMembershipInfoController.getlistCountryItems();
        girlJoinMembershipInfoController.primaryAddressSave();
        girlJoinMembershipInfoController.secondaryAddressSave();

        girlJoinMembershipInfoController.girlEmail = 'girlEmail@gmail.com';
        girlJoinMembershipInfoController.girlPhone = '23567';
        girlJoinMembershipInfoController.streetLine1 = 'STreet1';
        girlJoinMembershipInfoController.streetLine2 = 'STreet2';
        girlJoinMembershipInfoController.county = '2';
        girlJoinMembershipInfoController.country = 'country1';
        girlJoinMembershipInfoController.city = 'city1';
        girlJoinMembershipInfoController.state = 'State1';
        girlJoinMembershipInfoController.zipCode ='11111';
        girlJoinMembershipInfoController.secondaryTextOptIn = true;
        girlJoinMembershipInfoController.booleanOppMembershipOnPaper = true;
        girlJoinMembershipInfoController.selectedCustodialInfo = 'Parent';
        girlJoinMembershipInfoController.calculateGirlAge();
        girlJoinMembershipInfoController.showSecondaryContact();
        PageReference pageReference = girlJoinMembershipInfoController.submit();
        system.assertequals(string.valueOf(pageReference.getUrl()).contains('Girl_ThankYou'),true);
        Account councilAcc = [Select Id, Name, Volunteer_Financial_Aid_Available__c from Account where Id = :councilAccount.Id];
        Contact childContact1 = [Select Id, Name, rC_Bios__Preferred_Email__c from Contact Where Id = :childContact.Id];
        Contact parentContact1 = [Select Id, Name, rC_Bios__Home_Email__c, HomePhone from Contact Where Id = :parentContact.Id];
        system.assert(pageReference.getUrl().contains('Volunteer_PaymentProcessing'));
        system.assertEquals(councilAcc.Volunteer_Financial_Aid_Available__c, true);
        system.assertEquals(childContact1.rC_Bios__Preferred_Email__c, 'girlEmail@gmail.com');
        system.assertEquals(parentContact1.rC_Bios__Home_Email__c, 'primarycontact1@gmail.com');
        system.assertEquals(parentContact1.HomePhone, '2233445');
        girlJoinMembershipInfoController.submit();
        test.stopTest();
    }

        @isTest(SeeAllData=true)
        static void test_39_FinancialAidRequired() {
        List<Account> accountList = new List<Account>();
        List<Contact> contactList = new List<Contact>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        test.startTest();
        Account councilAccount = rC_GSATests.initializeAccount(true);
        councilAccount.Name = 'Girl Scouts of Eastern Pennsylvania, Inc.';
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        councilAccount.Volunteer_Financial_Aid_Available__c = true;
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(true);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        upsert accountList;

        Contact parentContact = rC_GSATests.initializeParentContact(householdAccount, true);
        parentContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        //parentContact.Birthdate = System.today();
        parentContact.AccountId = accountList[1].Id;
        contactList.add(parentContact);

        Contact childContact = rC_GSATests.initializeContact(householdAccount, true);
        //childContact.Birthdate = System.today();
        childContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        childContact.AccountId = accountList[1].Id;
        contactList.add(childContact);
        upsert contactList ;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, '12345', true);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        upsert troopOrGroupVolunteerProject1;

        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', troopOrGroupVolunteerProject1.Id, councilAccount.Id, null, true);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        upsert troopGroupVolunteerJob1;

        CampaignMember campaignMember = new CampaignMember(ContactId = childContact.Id, CampaignId= troopGroupVolunteerJob1.Id);
        upsert campaignMember;


        PageReference joinMembershipInformationPage = new PageReference('apex/Girl_JoinMembershipInformation');
        joinMembershipInformationPage.getParameters().put('ParentContactId', contactList[0].Id);
        joinMembershipInformationPage.getParameters().put('GirlContactId', contactList[1].Id);
        joinMembershipInformationPage.getParameters().put('CouncilId', accountList[0].Id);
        joinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMember.Id);
        test.setCurrentPage(joinMembershipInformationPage);

        Girl_JoinMembershipInfoController girlJoinMembershipInfoController = new Girl_JoinMembershipInfoController();

        List<PricebookEntry> PricebookEntryList = [Select Pricebook2.Description,
                                                          Pricebook2.IsActive,
                                                          Pricebook2.Name,
                                                          Pricebook2.Id,
                                                          Pricebook2Id,
                                                          Product2Id,
                                                          UnitPrice,
                                                          Name,
                                                          Id
                                                     From PricebookEntry
                                                    where Pricebook2.Name = 'Girl Scouts USA'
                                                      and Pricebook2.IsActive = true
        ];

        girlJoinMembershipInfoController.membershipProduct = (PricebookEntryList <> NULL && PricebookEntryList.size() > 0) ? PricebookEntryList[0].Id : NULL;
        girlJoinMembershipInfoController.primaryFirstName = 'PrimaryContact1';
        girlJoinMembershipInfoController.primaryLastName = 'PrimaryContact1';
        girlJoinMembershipInfoController.primaryEmail = 'PrimaryContact1@gmail.com';
        girlJoinMembershipInfoController.primaryPreferredEmail = 'Email';
        girlJoinMembershipInfoController.primaryGender = 'Female';
        girlJoinMembershipInfoController.primaryHomePhone = '2233445';
        girlJoinMembershipInfoController.primaryPreferredPhone = 'Home Phone';
        girlJoinMembershipInfoController.primaryStreetLine1 = 'STreet1';
        girlJoinMembershipInfoController.primaryStreetLine2 = 'STreet1';
        girlJoinMembershipInfoController.primaryCity = 'city1';
        girlJoinMembershipInfoController.primaryState = 'State1';
        girlJoinMembershipInfoController.primaryZipCode ='354666';
        girlJoinMembershipInfoController.primaryCounty = '2';
        girlJoinMembershipInfoController.primaryCountry = 'COuntry';
        girlJoinMembershipInfoController.primaryEmailOptIn = true;

        girlJoinMembershipInfoController.secondaryFirstName = 'PrimaryContact1';
        girlJoinMembershipInfoController.secondaryLastName = 'PrimaryContact1';
        girlJoinMembershipInfoController.secondaryEmail = 'PrimaryContact1@gmail.com';
        girlJoinMembershipInfoController.secondaryGender = 'Female';
        girlJoinMembershipInfoController.secondaryPreferredEmail = 'Email';
        girlJoinMembershipInfoController.secondaryHomePhone = '2233445';
        girlJoinMembershipInfoController.secondaryPreferredPhone = 'Home Phone';
        girlJoinMembershipInfoController.secondaryStreetLine1 = 'STreet1';
        girlJoinMembershipInfoController.secondaryStreetLine2 = 'STreet1';
        girlJoinMembershipInfoController.secondaryCity = 'city1';
        girlJoinMembershipInfoController.secondaryState = 'State1';
        girlJoinMembershipInfoController.secondaryZipCode ='354666';
        girlJoinMembershipInfoController.secondaryCounty = '2';
        girlJoinMembershipInfoController.secondaryCountry = 'COuntry';
        girlJoinMembershipInfoController.secondaryEmailOptIn = true;

        girlJoinMembershipInfoController.MobilePhone = '989898243';
        girlJoinMembershipInfoController.primaryTextOptIn = true;
        girlJoinMembershipInfoController.dateOfBirth = '04/07/1995';
        girlJoinMembershipInfoController.custodialFlag = true;
        girlJoinMembershipInfoController.booleanTermsAndConditions = true;

        girlJoinMembershipInfoController.girlEmail = 'girlEmail@gmail.com';
        girlJoinMembershipInfoController.girlPhone = '23567';
        girlJoinMembershipInfoController.streetLine1 = 'STreet1';
        girlJoinMembershipInfoController.streetLine2 = 'STreet2';
        girlJoinMembershipInfoController.county = '2';
        girlJoinMembershipInfoController.country = 'country1';
        girlJoinMembershipInfoController.city = 'city1';
        girlJoinMembershipInfoController.state = 'State1';
        girlJoinMembershipInfoController.zipCode ='354666';
       
       girlJoinMembershipInfoController.calculateGirlAge();
       girlJoinMembershipInfoController.secondaryTextOptIn = false;
       girlJoinMembershipInfoController.booleanOppMembershipOnPaper = true;
       girlJoinMembershipInfoController.booleanOpportunityGrantRequested = true;
       PageReference pageReference = girlJoinMembershipInfoController.submit();
       /*
       if(pageReference.getUrl() !=null && string.valueOf(pageReference.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pageReference.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                //system.assertEquals(pageName,'GIRL_THANKYOU');
        }
        */
        Girl_JoinMembershipInfoController.updateGirlContactInfo(childContact.Id, '04/07/1995', 'Parent', 'girlEmail@gmail.com', 'PrimaryContact1@gmail.com', '989898243', true, true, '989898243', false);
        test.stopTest();

        Account councilAcc = [Select Id, Name, Volunteer_Financial_Aid_Available__c from Account where Id = :councilAccount.Id];
        Contact childContact1 = [Select Id, Name, rC_Bios__Preferred_Email__c, rC_Bios__Home_Email__c from Contact Where Id = :childContact.Id];
        Contact parentContact1 = [Select Id, Name, rC_Bios__Home_Email__c, HomePhone from Contact Where Id = :parentContact.Id];
        system.assertEquals(councilAcc.Volunteer_Financial_Aid_Available__c, true);
        system.assertEquals(childContact1.rC_Bios__Preferred_Email__c, 'Home');
        system.assertEquals(childContact1.rC_Bios__Home_Email__c, 'girlemail@gmail.com');
        //system.assertEquals(parentContact1.rC_Bios__Home_Email__c, 'primarycontact1@gmail.com');

    }

    @isTest(SeeAllData=true)
        static void test_39_redirectToGirlPayment() {
        List<Account> accountList = new List<Account>();
        List<Contact> contactList = new List<Contact>();
        List<Campaign> volunteerJobsCampaignList = new List<Campaign>();
        test.startTest();
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.Name = 'Girl Scouts of Eastern Pennsylvania, Inc.';
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        councilAccount.Volunteer_Financial_Aid_Available__c = true;
        accountList.add(councilAccount);

        Account householdAccount = rC_GSATests.initializeAccount(false);
        householdAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        accountList.add(householdAccount);
        insert accountList;

        Contact parentContact = rC_GSATests.initializeParentContact(householdAccount, false);
        parentContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        parentContact.Birthdate = System.today();
        parentContact.AccountId = accountList[1].Id;
        contactList.add(parentContact);

        Contact childContact = rC_GSATests.initializeContact(householdAccount, false);
        childContact.Birthdate = System.today();
        childContact.RecordTypeId =  rC_GSATests.getContactRecordTypeId(rC_GSATests.HOUSEHOLD_RECORDTYPE);
        childContact.AccountId = accountList[1].Id;
        contactList.add(childContact);
        insert contactList ;

        Campaign troopOrGroupVolunteerProject1 = rC_GSATests.initializeCampaign('TroopGroupVolProj',null, councilAccount.Id, '12345', true);
        troopOrGroupVolunteerProject1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_PROJECT_RECORDTYPE);
        troopOrGroupVolunteerProject1.Display_on_Website__c = true;
        upsert troopOrGroupVolunteerProject1;

        Campaign troopGroupVolunteerJob1 = rC_GSATests.initializeCampaign('Troop Ledader1', troopOrGroupVolunteerProject1.Id, councilAccount.Id, null, true);
        troopGroupVolunteerJob1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId(rC_GSATests.VOLUNTEER_JOBS_RECORDTYPE);
        troopGroupVolunteerJob1.Display_on_Website__c = true;
        upsert troopGroupVolunteerJob1;

        CampaignMember campaignMember = new CampaignMember(ContactId = childContact.Id, CampaignId= troopGroupVolunteerJob1.Id);
        upsert campaignMember;


        PageReference joinMembershipInformationPage = new PageReference('apex/Girl_JoinMembershipInformation');
        joinMembershipInformationPage.getParameters().put('ParentContactId', contactList[0].Id);
        joinMembershipInformationPage.getParameters().put('GirlContactId', contactList[1].Id);
        joinMembershipInformationPage.getParameters().put('CouncilId', accountList[0].Id);
        joinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMember.Id);
        test.setCurrentPage(joinMembershipInformationPage);

        Girl_JoinMembershipInfoController girlJoinMembershipInfoController = new Girl_JoinMembershipInfoController();

        List<PricebookEntry> PricebookEntryList = [Select Pricebook2.Description,
                                                          Pricebook2.IsActive,
                                                          Pricebook2.Name,
                                                          Pricebook2.Id,
                                                          Pricebook2Id,
                                                          Product2Id,
                                                          UnitPrice,
                                                          Name,
                                                          Id
                                                     From PricebookEntry
                                                    where Pricebook2.Name = 'Girl Scouts USA'
                                                      and Pricebook2.IsActive = true
        ];

        girlJoinMembershipInfoController.membershipProduct = (PricebookEntryList <> NULL && PricebookEntryList.size() > 0) ? PricebookEntryList[0].Id : NULL;
        girlJoinMembershipInfoController.primaryFirstName = 'PrimaryContact1';
        girlJoinMembershipInfoController.primaryLastName = 'PrimaryContact1';
        girlJoinMembershipInfoController.primaryEmail = 'PrimaryContact1@gmail.com';
        girlJoinMembershipInfoController.primaryPreferredEmail = 'Email';
        girlJoinMembershipInfoController.primaryGender = 'Female';
        girlJoinMembershipInfoController.primaryHomePhone = '2233445';
        girlJoinMembershipInfoController.primaryPreferredPhone = 'Home Phone';
        girlJoinMembershipInfoController.primaryStreetLine1 = 'STreet1';
        girlJoinMembershipInfoController.primaryStreetLine2 = 'STreet1';
        girlJoinMembershipInfoController.primaryCity = 'city1';
        girlJoinMembershipInfoController.primaryState = 'State1';
        girlJoinMembershipInfoController.primaryZipCode ='354666';
        girlJoinMembershipInfoController.primaryCounty = '2';
        girlJoinMembershipInfoController.primaryCountry = 'COuntry';
        girlJoinMembershipInfoController.primaryEmailOptIn = true;

        girlJoinMembershipInfoController.secondaryFirstName = 'PrimaryContact1';
        girlJoinMembershipInfoController.secondaryLastName = 'PrimaryContact1';
        girlJoinMembershipInfoController.secondaryEmail = 'PrimaryContact1@gmail.com';
        girlJoinMembershipInfoController.secondaryGender = 'Female';
        girlJoinMembershipInfoController.secondaryPreferredEmail = 'Email';
        girlJoinMembershipInfoController.secondaryHomePhone = '2233445';
        girlJoinMembershipInfoController.secondaryPreferredPhone = 'Home Phone';
        girlJoinMembershipInfoController.secondaryStreetLine1 = 'STreet1';
        girlJoinMembershipInfoController.secondaryStreetLine2 = 'STreet1';
        girlJoinMembershipInfoController.secondaryCity = 'city1';
        girlJoinMembershipInfoController.secondaryState = 'State1';
        girlJoinMembershipInfoController.secondaryZipCode ='354666';
        girlJoinMembershipInfoController.secondaryCounty = '2';
        girlJoinMembershipInfoController.secondaryCountry = 'COuntry';
        girlJoinMembershipInfoController.secondaryEmailOptIn = true;

        girlJoinMembershipInfoController.MobilePhone = '989898243';
        girlJoinMembershipInfoController.primaryTextOptIn = true;
        girlJoinMembershipInfoController.dateOfBirth = '04/07/2014';
        girlJoinMembershipInfoController.custodialFlag = true;
        girlJoinMembershipInfoController.booleanTermsAndConditions = true;

        girlJoinMembershipInfoController.getPreferredEmails();

        girlJoinMembershipInfoController.girlEmail = 'girlEmail@gmail.com';
        girlJoinMembershipInfoController.girlPhone = '23567';
        girlJoinMembershipInfoController.streetLine1 = 'STreet1';
        girlJoinMembershipInfoController.streetLine2 = 'STreet2';
        girlJoinMembershipInfoController.county = '2';
        girlJoinMembershipInfoController.country = 'country1';
        girlJoinMembershipInfoController.city = 'city1';
        girlJoinMembershipInfoController.state = 'State1';
        girlJoinMembershipInfoController.zipCode ='354666';
       girlJoinMembershipInfoController.secondaryTextOptIn = false;
       girlJoinMembershipInfoController.booleanOppMembershipOnPaper = false;
       girlJoinMembershipInfoController.booleanOpportunityGrantRequested = false;
       PageReference pageReference = girlJoinMembershipInfoController.submit();
       /*
       if(pageReference.getUrl() !=null && string.valueOf(pageReference.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pageReference.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                //system.assertEquals(pageName,'GIRL_PAYMENTPROCESSING');
        }
        */
        Girl_JoinMembershipInfoController.updateGirlContactInfo(childContact.Id, '04/07/1995', 'Parent', 'girlEmail@gmail.com', 'PrimaryContact1@gmail.com', '989898243', true, true, '989898243', false);
        test.stopTest();

        Account councilAcc = [Select Id, Name, Volunteer_Financial_Aid_Available__c from Account where Id = :councilAccount.Id];
        Contact childContact1 = [Select Id, Name, rC_Bios__Preferred_Email__c, rC_Bios__Home_Email__c from Contact Where Id = :childContact.Id];
        Contact parentContact1 = [Select Id, Name, rC_Bios__Home_Email__c, HomePhone from Contact Where Id = :parentContact.Id];
        system.assertEquals(councilAcc.Volunteer_Financial_Aid_Available__c, true);
        system.assertEquals(childContact1.rC_Bios__Preferred_Email__c, 'Home');
        system.assertEquals(childContact1.rC_Bios__Home_Email__c, 'girlemail@gmail.com');
        //system.assertEquals(parentContact1.rC_Bios__Home_Email__c, 'primarycontact1@gmail.com');
    }

    @isTest(SeeAllData=true)
    static void test_40_PayWithCreditCard(){

        Account account = rC_GSATests.initializeAccount(false);
        account.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert account;

        Contact contact = rC_GSATests.initializeContact(account, true);

        Campaign campaign = rC_GSATests.initializeCampaignNew(account.Id, '12345', true);

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id,campaign.Id,true);

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account, campaign, true);

        Pricebook2 pricebook2 = rC_GSATests.initializePricebook2( true);
        Pricebook2 standardPriceBook = [
            select id
              from Pricebook2
             where isStandard = true
             limit 1
        ];
        Product2 product2 = rC_GSATests.initializeProduct2( true);
        PricebookEntry standardPricebookEntry = rC_GSATests.initializePricebookEntry(standardPriceBook,product2, true);
        OpportunityLineItem opportunityLineItem =  rC_GSATests.initializeOpportunityLineItem(standardPricebookEntry, opportunity, true);

        test.startTest();
        Pagereference girlPaymentProcessingPage = Page.Girl_PaymentProcessing;//new Pagereference('/apex/Girl_PaymentProcessing');
        girlPaymentProcessingPage.getParameters().put('GirlContactId', contact.Id);
        girlPaymentProcessingPage.getParameters().put('OpportunityId', opportunity.Id);
        girlPaymentProcessingPage.getParameters().put('CampaignMemberIds', campaignMember.Id);
        girlPaymentProcessingPage.getParameters().put('CouncilId', account.Id);
        test.setCurrentPage(girlPaymentProcessingPage);

        system.debug('girlPaymentProcessingPage===>'+girlPaymentProcessingPage);

        GirlPaymentProcessingController girlPaymentProcessingController = new GirlPaymentProcessingController();
        girlPaymentProcessingController.contactId = contact.Id;
        girlPaymentProcessingController.councilId = account.Id;
        girlPaymentProcessingController.opportunityId = opportunity.Id;
        girlPaymentProcessingController.campaignMembersId = campaignMember.Id;

        girlPaymentProcessingController.firstName = 'firstName';
        girlPaymentProcessingController.lastName = 'lastName';
        girlPaymentProcessingController.address = 'testAddress';
        girlPaymentProcessingController.city = 'testCity';
        girlPaymentProcessingController.country = 'testCountry';
        girlPaymentProcessingController.state = 'testState';
        girlPaymentProcessingController.zipCode= 'testZipCode';
        girlPaymentProcessingController.cardHolderName= 'testCard';
        girlPaymentProcessingController.cardNumber= '4111111111111111';
        girlPaymentProcessingController.expMonth= '01';
        girlPaymentProcessingController.expYear= '19';
        girlPaymentProcessingController.securityCode= '123';
        girlPaymentProcessingController.acceptGSPromiseAndLaw= true;
        girlPaymentProcessingController.amountValue= 15;

        girlPaymentProcessingController.getlistCountryItems();
        girlPaymentProcessingController.getlistexpMonth();
        girlPaymentProcessingController.getlistexpYear();
        girlPaymentProcessingController.getlistStateItems();

        PageReference paymentPageSelectRadio = girlPaymentProcessingController.createTransactionRecord();

        system.assert(girlPaymentProcessingController.opportunityTransactionList.size()>0);

        PageReference paymentPage = girlPaymentProcessingController.processMyOrder();

        test.stopTest();
    }



    static testMethod void test_40_nextButtonLeadingToDemographicsInformationPage() {
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
        Opportunity opportunity = rC_GSATests.initializeOpportunity(accountCouncil, campaign, true);

        PageReference pageRef = Page.Girl_ThankYou;
        test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('GirlContactId',contact.Id);
            ApexPages.currentPage().getParameters().put('CouncilId',accountCouncil.Id);
            ApexPages.currentPage().getParameters().put('OpportunityId',opportunity.Id);
            ApexPages.currentPage().getParameters().put('CampaignMemberIds',campaignMember.Id);
            Apexpages.currentPage().getParameters().put('CashOrCheck','true');
            Apexpages.currentPage().getParameters().put('FinancialAidRequired','true');
        test.startTest();
        Girl_ThankYouPageController girlThankyouPageController = new Girl_ThankYouPageController();
        Pagereference pagerefernce = girlThankyouPageController.submit();
        test.stopTest();
        if(pagerefernce.getUrl() !=null && string.valueOf(pagerefernce.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pagerefernce.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'GIRL_DEMOGRAPHICSINFORMATION');
        }
    }

    static testMethod void test_42_backGroundCheck() {
        List<SelectOption> lstOptions = new List<SelectOption>();
        Account councilAccount = rC_GSATests.initializeAccount(false);
        councilAccount.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert councilAccount;

        Contact contact = rC_GSATests.initializeContact(councilAccount, false);
        contact.firstName = 'LoginUSer543';
        contact.lastName = 'User433';
        contact.rC_Bios__Home_Email__c ='LoginUSer543+678@test.com';
        insert contact;

        Campaign campaign = rC_GSATests.initializeCampaign('CampaignTest',null, councilAccount.Id, string.ValueOf(41101), false);
        campaign.Special_Handling__c  = false;
        campaign.Background_Check_Needed__c = true;
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(councilAccount,campaign,true);
        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, true);

        PageReference pageRef = Page.Volunteer_DemographicsInformation;
        test.setCurrentPage(pageRef);
            ApexPages.currentPage().getParameters().put('GirlContactId',contact.Id);
            ApexPages.currentPage().getParameters().put('CouncilId',councilAccount.Id);
            ApexPages.currentPage().getParameters().put('CampaignMemberIds',campaignMember.Id);
            ApexPages.currentPage().getParameters().put('OpportunityId',opportunity.Id);


        test.startTest();
        Girl_DemographicsInformationController girlDemographicsInfoController = new Girl_DemographicsInformationController();
        girlDemographicsInfoController.selectedEthnicity = 'I choose not to share';
        girlDemographicsInfoController.lstSelectedCampaignFields.add(new SelectOption('Asian','Asian'));
        girlDemographicsInfoController.getEthnicityOptionList();
        girlDemographicsInfoController.getRaceOptionList();
        Pagereference pagerefernce = girlDemographicsInfoController.submit();
        test.stopTest();
        if(pagerefernce.getUrl() !=null && string.valueOf(pagerefernce.getUrl()).contains('?')){
                String pagereferenceURL = string.valueOf(pagerefernce.getUrl());
                string[] url = pagereferenceURL.split('\\?');
                String[] splitURL = url[0].split('/');
                String pageName = splitURL[2].toUpperCase();
                system.assertEquals(pageName,'GIRL_DEMOGRAPHICSTHANKYOU');
        }
    }


   static testMethod void test_43_backGroundCheckNotNeededAndPortalUser() {
        Account account = rC_GSATests.initializeAccount(false);
        account.RecordTypeId = rC_GSATests.getAccountRecordTypeId(rC_GSATests.COUNCIL_RECORDTYPE);
        insert account;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.firstName = 'AlexUSer530';
        contact.lastName = 'AlexUSer530';
        contact.rC_Bios__Home_Email__c ='AlexUSer530+530@test.com';
        insert contact;

        Campaign campaign = rC_GSATests.initializeCampaign('CampaignTestTemp',null, account.Id, string.ValueOf(41101), false);
        campaign.Special_Handling__c = false;
        campaign.Background_Check_Needed__c = false;
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Type = 'Girl Membership';
        insert opportunity;

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Membership__c = opportunity.Id;
        insert campaignMember;

        system.debug('campaignMember==>'+campaignMember);

        Profile profile = [Select p.Id From Profile p where Name = 'Partner Community Login Custom'];

        Member_Community_Profile__c memberCommunityProfile = new Member_Community_Profile__c();
        memberCommunityProfile.Name = 'Member Community Profile Id';
        memberCommunityProfile.ProfileId__c = profile.Id;
        insert memberCommunityProfile;

        PageReference pageRef = Page.Volunteer_DemographicsInformation;
        test.setCurrentPage(pageRef);
        ApexPages.currentPage().getParameters().put('GirlContactId',contact.Id);
        ApexPages.currentPage().getParameters().put('CouncilId',account.Id);
        ApexPages.currentPage().getParameters().put('CampaignMemberIds',campaignMember.Id);
        ApexPages.currentPage().getParameters().put('OpportunityId',opportunity.Id);

        test.startTest();

        Girl_DemographicsInformationController girlDemographicsInfoController = new Girl_DemographicsInformationController();
        girlDemographicsInfoController.selectedEthnicity = 'I choose not to share';
        girlDemographicsInfoController.lstSelectedCampaignFields.add(new SelectOption('Asian','Asian'));
        girlDemographicsInfoController.getRaceOptionList();
        girlDemographicsInfoController.getEthnicityOptionList();
        girlDemographicsInfoController.submit();

        test.stopTest();
        CampaignMember campaignMember1 = [Select Membership__r.Membership_Status__c
                                               , Membership__r.StageName
                                               , Membership__r.Name
                                               , Membership__r.Id
                                               , Membership__c
                                               , Id
                                           From CampaignMember
                                           where Id = :campaignMember.Id
        ];

        if(campaignMember1 != null && campaignMember1.Id != null && campaignMember1.Membership__r != null){
            List<Opportunity> opportunityList = [
                Select StageName
                     , Name
                     , Membership_Status__c
                     , Id
                  From Opportunity
                 where Id = :campaignMember1.Membership__r.Id
             ];
            Opportunity opportunity1 = (opportunityList != null && opportunityList.size() > 0) ? opportunityList[0] : new Opportunity();

            if(opportunity1 != null && opportunity1.Id != null){
                opportunity1.StageName = 'Completed';
                opportunity1.Membership_Status__c = 'Welcome';
                database.Saveresult dbUpsert = Database.update(opportunity1);
            }
        }
        List<User> userList =
            [Select Id
                    , Email
                    , Alias
                    , ContactId
               From User
              Where username =:'AlexUSer530+530@test.com'
             limit 1
            ];

        system.assertEquals(userList[0].Email, 'alexuser530+530@test.com');
        system.assertEquals(userList[0].Alias, 'AAl');
        system.assertEquals(userList[0].ContactId, contact.Id);
    }

    @isTest(seeAllData=true)
        static  void test_89_notEligibleForRoleRenewal() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign1001',null,null,null,false);
        campaign.Participation__c = 'Event';
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
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

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Membership__c = opportunity.Id;
        insert campaignMember;

        test.startTest();
        system.runAs(portalUser) {
        VolunteerRenewal_RenewalController objVolunteerRenewalController = new VolunteerRenewal_RenewalController();
        objVolunteerRenewalController.isInterestedInSearchingForOtherRole = 'Yes';
        objVolunteerRenewalController.displayWantToBeAnAdultMember();
        objVolunteerRenewalController.displayWantToSearchForOtherRoles();
        objVolunteerRenewalController.renewPosition();
        objVolunteerRenewalController.displayCampaignMember();
        test.stopTest();
        }
        List<Apexpages.Message> msgs = ApexPages.getMessages();
        boolean b = false;
        for(Apexpages.Message msg:msgs) {
        if (msg.getDetail().contains('You are not eligible to renew role'))
            b = true;
        }
        system.assert(b);
   }


   //scenario 1
   @isTest(seeAllData=true)
   static void test_89_renewAllRoleAndSelectNewRole() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        List<Campaign> campaignInsertList = new List<Campaign>();
        Campaign parentCampaign = rC_GSATests.initializeCampaign('Campaign11',null,null,null,false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Project');
        parentCampaign.Participation__c = 'Troop';
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign101',parentCampaign.Id,null,null,false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        campaign.Participation__c = 'Troop';
        campaign.Program_Grade_Level__c = '1-Daisy';
        campaignInsertList.add(campaign);

        Campaign secondCampaign = rC_GSATests.initializeCampaign('Campaign1001',parentCampaign.Id,null,null,false);
        secondCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        secondCampaign.Program_Grade_Level__c = '1-Daisy';
        secondCampaign.Participation__c = 'Troop';
        campaignInsertList.add(secondCampaign);

        Campaign thirdCampaign = rC_GSATests.initializeCampaign('Campaign10001',parentCampaign.Id,null,null,false);
        thirdCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        thirdCampaign.Participation__c = 'Troop';
        thirdCampaign.Program_Grade_Level__c = '1-Daisy';
        campaignInsertList.add(thirdCampaign);
        insert campaignInsertList;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;

        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
        contact.MailingPostalCode = '12345';
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

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;

        CampaignMember secondCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, secondCampaign.Id, false);
        secondCampaignMember.Active__c = true;
        secondCampaignMember.Membership__c = opportunity.Id;
        secondCampaignMember.Display_Renewal__c = true;
        secondCampaignMember.Assignment_Type__c = 'Volunteer';
        insert secondCampaignMember;

        CampaignMember thirdCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, thirdCampaign.Id, false);
        thirdCampaignMember.Active__c = true;
        thirdCampaignMember.Membership__c = opportunity.Id;
        thirdCampaignMember.Display_Renewal__c = true;
        thirdCampaignMember.Assignment_Type__c = 'Volunteer';
        insert thirdCampaignMember;

        test.startTest();
        system.runAs(portalUser) {
        boolean originalValue;
        rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
        objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
        originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
        objCampaignMemberSetting.rC_Event__Disable_All__c = true;
        upsert objCampaignMemberSetting;

        VolunteerRenewal_RenewalController objVolunteerRenewalController = new VolunteerRenewal_RenewalController();
        objVolunteerRenewalController.displayCampaignMember();
        objVolunteerRenewalController.volunteerPositionRenewalList[0].continuePosition = 'Yes';
        objVolunteerRenewalController.volunteerPositionRenewalList[1].continuePosition = 'Yes';
        objVolunteerRenewalController.volunteerPositionRenewalList[2].continuePosition = 'Yes';
        objVolunteerRenewalController.getContinueThisPosition();
        objVolunteerRenewalController.getContinueThisPositionYesOrNo();
        objVolunteerRenewalController.getWantToBeAnAdultMember();
        objVolunteerRenewalController.getInterestedInSearchingForOtherRole();
        objVolunteerRenewalController.isInterestedInSearchingForOtherRole = 'Yes';
        Pagereference pageReference = objVolunteerRenewalController.renewPosition();
        system.assert(string.valueOf(pageReference.getUrl()).contains('VolunteerRenewal_TroopGroupRoleSearch'));

        objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
        update objCampaignMemberSetting;
        test.stopTest();
        }


   }


  //scenario 2
   @isTest(seeAllData=true)
   static void test_89_renewSomeRoleAndSelectNewRole() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        List<Campaign> campaignInsertList = new List<Campaign>();
        Campaign parentCampaign = rC_GSATests.initializeCampaign('Campaign11',null,null,null,false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Project');
        parentCampaign.Participation__c = 'Troop';
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign101',parentCampaign.Id,null,null,false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        campaign.Participation__c = 'Troop';
        campaign.Program_Grade_Level__c = '1-Daisy';
        campaignInsertList.add(campaign);

        Campaign secondCampaign = rC_GSATests.initializeCampaign('Campaign1001',parentCampaign.Id,null,null,false);
        secondCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        secondCampaign.Program_Grade_Level__c = '1-Daisy';
        secondCampaign.Participation__c = 'Troop';
        campaignInsertList.add(secondCampaign);

        Campaign thirdCampaign = rC_GSATests.initializeCampaign('Campaign10001',parentCampaign.Id,null,null,false);
        thirdCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        thirdCampaign.Participation__c = 'Troop';
        thirdCampaign.Program_Grade_Level__c = '1-Daisy';
        campaignInsertList.add(thirdCampaign);
        insert campaignInsertList;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
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

        List<CampaignMember> campaignMemberList = new List<CampaignMember>();


        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        campaignMemberList.add(campaignMember);

        CampaignMember secondCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, secondCampaign.Id, false);
        secondCampaignMember.Active__c = true;
        secondCampaignMember.Membership__c = opportunity.Id;
        secondCampaignMember.Display_Renewal__c = true;
        secondCampaignMember.Assignment_Type__c = 'Volunteer';
        campaignMemberList.add(secondCampaignMember);

        CampaignMember thirdCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, thirdCampaign.Id, false);
        thirdCampaignMember.Active__c = true;
        thirdCampaignMember.Membership__c = opportunity.Id;
        thirdCampaignMember.Display_Renewal__c = true;
        thirdCampaignMember.Assignment_Type__c = 'Volunteer';
        campaignMemberList.add(thirdCampaignMember);
        insert campaignMemberList;

        test.startTest();
        system.runAs(portalUser) {
            boolean originalValue;
            rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
            objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
            originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
            objCampaignMemberSetting.rC_Event__Disable_All__c = true;
            upsert objCampaignMemberSetting;
            VolunteerRenewal_RenewalController objVolunteerRenewalController = new VolunteerRenewal_RenewalController();
            objVolunteerRenewalController.displayCampaignMember();
            objVolunteerRenewalController.volunteerPositionRenewalList[0].continuePosition = 'Yes';
            objVolunteerRenewalController.volunteerPositionRenewalList[1].continuePosition = 'No';
            objVolunteerRenewalController.volunteerPositionRenewalList[2].continuePosition = 'Yes';
            objVolunteerRenewalController.isInterestedInSearchingForOtherRole = 'Yes';

            Pagereference pageReference = objVolunteerRenewalController.renewPosition();
            List<CampaignMember> campignMembers = VolunteerRegistrationUtilty.campaignMembers(objVolunteerRenewalController.volunteerPositionRenewalList[1].contactName, objVolunteerRenewalController.volunteerPositionRenewalList[1].positon);
            system.assert(string.valueOf(pageReference.getUrl()).contains('VolunteerRenewal_TroopGroupRoleSearch'));
            system.assertEquals(campignMembers[0].Display_Renewal__c,false);

            objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
            update objCampaignMemberSetting;
            test.stopTest();
        }
   }


    //scenario 3
    @isTest(seeAllData=true)
    static void test_89_renewAllRoleAndDoNotSelectNewRole() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        List<Campaign> campaignInsertList = new List<Campaign>();
        Campaign parentCampaign = rC_GSATests.initializeCampaign('Campaign11',null,null,null,false);
        parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Project');
        parentCampaign.Participation__c = 'Troop';
        parentCampaign.Program_Grade_Level__c = '1-Daisy';
        insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign101',parentCampaign.Id,null,null,false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        campaign.Participation__c = 'Troop';
        campaign.Program_Grade_Level__c = '1-Daisy';
        campaignInsertList.add(campaign);

        Campaign secondCampaign = rC_GSATests.initializeCampaign('Campaign1001',parentCampaign.Id,null,null,false);
        secondCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        secondCampaign.Program_Grade_Level__c = '1-Daisy';
        secondCampaign.Participation__c = 'Troop';
        campaignInsertList.add(secondCampaign);

        Campaign thirdCampaign = rC_GSATests.initializeCampaign('Campaign10001',parentCampaign.Id,null,null,false);
        thirdCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        thirdCampaign.Participation__c = 'Troop';
        thirdCampaign.Program_Grade_Level__c = '1-Daisy';
        campaignInsertList.add(thirdCampaign);
        insert campaignInsertList;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
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

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;

        CampaignMember secondCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, secondCampaign.Id, false);
        secondCampaignMember.Active__c = true;
        secondCampaignMember.Membership__c = opportunity.Id;
        secondCampaignMember.Display_Renewal__c = true;
        secondCampaignMember.Assignment_Type__c = 'Volunteer';
        insert secondCampaignMember;

        CampaignMember thirdCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, thirdCampaign.Id, false);
        thirdCampaignMember.Active__c = true;
        thirdCampaignMember.Membership__c = opportunity.Id;
        thirdCampaignMember.Display_Renewal__c = true;
        thirdCampaignMember.Assignment_Type__c = 'Volunteer';
        insert thirdCampaignMember;

        test.startTest();
        system.runAs(portalUser) {
        boolean originalValue;
        rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
        objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
        originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
        objCampaignMemberSetting.rC_Event__Disable_All__c = true;
        upsert objCampaignMemberSetting;

        VolunteerRenewal_RenewalController objVolunteerRenewalController = new VolunteerRenewal_RenewalController();
        objVolunteerRenewalController.displayCampaignMember();

        objVolunteerRenewalController.volunteerPositionRenewalList[0].continuePosition = 'Yes';
        objVolunteerRenewalController.volunteerPositionRenewalList[1].continuePosition = 'Yes';
        objVolunteerRenewalController.volunteerPositionRenewalList[2].continuePosition = 'Yes';
        objVolunteerRenewalController.isInterestedInSearchingForOtherRole = 'No';
        Pagereference pageReference = objVolunteerRenewalController.renewPosition();
        system.assert(string.valueOf(pageReference.getUrl()).contains('VolunteerRenewal_MembershipInformation'));

        objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
        update objCampaignMemberSetting;
        test.stopTest();
        }
   }



   //scenario 4
    @isTest(seeAllData=true)
    static  void test_89_renewSomeRoleAndDoNotSelectNewRole() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

         List<Campaign> campaignInsertList = new List<Campaign>();
         Campaign parentCampaign = rC_GSATests.initializeCampaign('Campaign11',null,null,null,false);
         parentCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Project');
         parentCampaign.Participation__c = 'Troop';
         parentCampaign.Program_Grade_Level__c = '1-Daisy';
         insert parentCampaign;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign101',parentCampaign.Id,null,null,false);
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        campaign.Participation__c = 'Troop';
        campaign.Program_Grade_Level__c = '1-Daisy';
        campaignInsertList.add(campaign);

        Campaign secondCampaign = rC_GSATests.initializeCampaign('Campaign1001',parentCampaign.Id,null,null,false);
        secondCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        secondCampaign.Program_Grade_Level__c = '1-Daisy';
        secondCampaign.Participation__c = 'Troop';
        campaignInsertList.add(secondCampaign);

        Campaign thirdCampaign = rC_GSATests.initializeCampaign('Campaign10001',parentCampaign.Id,null,null,false);
        thirdCampaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');
        thirdCampaign.Participation__c = 'Troop';
        thirdCampaign.Program_Grade_Level__c = '1-Daisy';
        campaignInsertList.add(thirdCampaign);
        insert campaignInsertList;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
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

        List<CampaignMember> campaignMemberList = new List<CampaignMember>();

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        campaignMemberList.add(campaignMember);

        CampaignMember secondCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, secondCampaign.Id, false);
        secondCampaignMember.Active__c = true;
        secondCampaignMember.Membership__c = opportunity.Id;
        secondCampaignMember.Display_Renewal__c = true;
        secondCampaignMember.Assignment_Type__c = 'Volunteer';
        campaignMemberList.add(secondCampaignMember);

        CampaignMember thirdCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, thirdCampaign.Id, false);
        thirdCampaignMember.Active__c = true;
        thirdCampaignMember.Membership__c = opportunity.Id;
        thirdCampaignMember.Display_Renewal__c = true;
        thirdCampaignMember.Assignment_Type__c = 'Volunteer';
        campaignMemberList.add(thirdCampaignMember);
        insert campaignMemberList;

        test.startTest();
        system.runAs(portalUser) {
            boolean originalValue;
            rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
            objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
            originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
            objCampaignMemberSetting.rC_Event__Disable_All__c = true;
            upsert objCampaignMemberSetting;
            VolunteerRenewal_RenewalController objVolunteerRenewalController = new VolunteerRenewal_RenewalController();
            objVolunteerRenewalController.displayCampaignMember();
            objVolunteerRenewalController.volunteerPositionRenewalList[0].continuePosition = 'No';
            objVolunteerRenewalController.volunteerPositionRenewalList[1].continuePosition = 'Yes';
            objVolunteerRenewalController.volunteerPositionRenewalList[2].continuePosition = 'Yes';
            objVolunteerRenewalController.isInterestedInSearchingForOtherRole = 'No';
            Pagereference pageReference = objVolunteerRenewalController.renewPosition();
            List<CampaignMember> campignMembers = VolunteerRegistrationUtilty.campaignMembers(objVolunteerRenewalController.volunteerPositionRenewalList[0].contactName, objVolunteerRenewalController.volunteerPositionRenewalList[0].positon);
            system.assertEquals(campignMembers[0].Display_Renewal__c,false);
            system.assert(string.valueOf(pageReference.getUrl()).contains('VolunteerRenewal_MembershipInformation'));

            objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
            update objCampaignMemberSetting;
        test.stopTest();
        }
    }


    //scenario 5
     @isTest(seeAllData=true)
    static void test_89_doNotRenewAllRoleAndAnAdultMemberAndSelectNewRole() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign101',null,null,null,false);
        campaign.Participation__c = 'Troop';
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        Campaign secondCampaign = rC_GSATests.initializeCampaign('Campaign1001',null,null,null,false);
        secondCampaign.Participation__c = 'Troop';
        secondCampaign.Program_Grade_Level__c = '1-Daisy';
        insert secondCampaign;

        Campaign thirdCampaign = rC_GSATests.initializeCampaign('Campaign10001',null,null,null,false);
        thirdCampaign.Participation__c = 'Troop';
        thirdCampaign.Program_Grade_Level__c = '1-Daisy';
        insert thirdCampaign;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
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

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;

        CampaignMember secondCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, secondCampaign.Id, false);
        secondCampaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        secondCampaignMember.Display_Renewal__c = true;
        secondCampaignMember.Assignment_Type__c = 'Volunteer';
        insert secondCampaignMember;

        CampaignMember thirdCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, thirdCampaign.Id, false);
        thirdCampaignMember.Active__c = true;
        thirdCampaignMember.Membership__c = opportunity.Id;
        thirdCampaignMember.Display_Renewal__c = true;
        thirdCampaignMember.Assignment_Type__c = 'Volunteer';
        insert thirdCampaignMember;

        test.startTest();
        system.runAs(portalUser) {
        boolean originalValue;
        rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
        objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
        originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
        objCampaignMemberSetting.rC_Event__Disable_All__c = true;
        upsert objCampaignMemberSetting;
        VolunteerRenewal_RenewalController objVolunteerRenewalController = new VolunteerRenewal_RenewalController();
        objVolunteerRenewalController.displayCampaignMember();
        objVolunteerRenewalController.volunteerPositionRenewalList[0].continuePosition = 'No';
        objVolunteerRenewalController.volunteerPositionRenewalList[1].continuePosition = 'No';
        objVolunteerRenewalController.volunteerPositionRenewalList[2].continuePosition = 'No';
        objVolunteerRenewalController.isInterestedInSearchingForOtherRole = 'Yes';
        objVolunteerRenewalController.isWantToBeAnAdultMember = 'Yes';
        objVolunteerRenewalController.hideWantToSearchForOtherRoles();
        Pagereference pageReference = objVolunteerRenewalController.renewPosition();
        List<CampaignMember> campignMembers = VolunteerRegistrationUtilty.campaignMembers(objVolunteerRenewalController.volunteerPositionRenewalList[0].contactName, objVolunteerRenewalController.volunteerPositionRenewalList[0].positon);
        system.assertEquals(campignMembers[0].Display_Renewal__c,false);
        system.assert(string.valueOf(pageReference.getUrl()).contains('VolunteerRenewal_TroopGroupRoleSearch'));

        objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
        update objCampaignMemberSetting;
        test.stopTest();
        }
    }


    //scenario 6
     @isTest(seeAllData=true)
    static void test_89_doNotRenewAllRoleAndAnAdultMemberAndDoNotSelectNewRole() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign101',null,null,null,false);
        campaign.Participation__c = 'Troop';
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        Campaign secondCampaign = rC_GSATests.initializeCampaign('Campaign1001',null,null,null,false);
        secondCampaign.Program_Grade_Level__c = '1-Daisy';
        secondCampaign.Participation__c = 'Troop';
        insert secondCampaign;

        Campaign thirdCampaign = rC_GSATests.initializeCampaign('Campaign10001',null,null,null,false);
        thirdCampaign.Participation__c = 'Troop';
        thirdCampaign.Program_Grade_Level__c = '1-Daisy';
        insert thirdCampaign;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
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

        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;

        CampaignMember secondCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, secondCampaign.Id, false);
        secondCampaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        secondCampaignMember.Display_Renewal__c = true;
        secondCampaignMember.Assignment_Type__c = 'Volunteer';
        insert secondCampaignMember;

        CampaignMember thirdCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, thirdCampaign.Id, false);
        thirdCampaignMember.Active__c = true;
        thirdCampaignMember.Membership__c = opportunity.Id;
        thirdCampaignMember.Display_Renewal__c = true;
        thirdCampaignMember.Assignment_Type__c = 'Volunteer';
        insert thirdCampaignMember;

        test.startTest();
        system.runAs(portalUser) {
        boolean originalValue;
        rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
        objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
        originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
        objCampaignMemberSetting.rC_Event__Disable_All__c = true;
        upsert objCampaignMemberSetting;
        VolunteerRenewal_RenewalController objVolunteerRenewalController = new VolunteerRenewal_RenewalController();
        objVolunteerRenewalController.displayCampaignMember();

        objVolunteerRenewalController.volunteerPositionRenewalList[0].continuePosition = 'No';
        objVolunteerRenewalController.volunteerPositionRenewalList[1].continuePosition = 'No';
        objVolunteerRenewalController.volunteerPositionRenewalList[2].continuePosition = 'No';
        objVolunteerRenewalController.isInterestedInSearchingForOtherRole = 'No';
        objVolunteerRenewalController.isWantToBeAnAdultMember = 'Yes';
        Pagereference pageReference = objVolunteerRenewalController.renewPosition();
        List<CampaignMember> campignMembers = VolunteerRegistrationUtilty.campaignMembers(objVolunteerRenewalController.volunteerPositionRenewalList[0].contactName, objVolunteerRenewalController.volunteerPositionRenewalList[0].positon);
        system.assertEquals(campignMembers[0].Display_Renewal__c,false);
        system.assert(string.valueOf(pageReference.getUrl()).contains('VolunteerRenewal_MembershipInformation'));

        objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
        update objCampaignMemberSetting;
        test.stopTest();
        }
    }

    //scenario 7
     @isTest(seeAllData=true)
    static  void test_89_doNotRenewAllRoleAndNotAnAdultMemberAndDoNotSelectNewRole() {
        ID ProfileID = [ Select Id from Profile where Name = 'Partner Community Login Custom'][0].id;
        Account account = rC_GSATests.initializeAccount(false);
        account.Name = 'Pankaj';
        insert account;

        Campaign campaign = rC_GSATests.initializeCampaign('Campaign101',null,null,null,false);
        campaign.Participation__c = 'Troop';
        campaign.Program_Grade_Level__c = '1-Daisy';
        insert campaign;

        Campaign secondCampaign = rC_GSATests.initializeCampaign('Campaign1001',null,null,null,false);
        secondCampaign.Participation__c = 'Troop';
        secondCampaign.Program_Grade_Level__c = '1-Daisy';
        insert secondCampaign;

        Campaign thirdCampaign = rC_GSATests.initializeCampaign('Campaign10001',null,null,null,false);
        thirdCampaign.Participation__c = 'Troop';
        thirdCampaign.Program_Grade_Level__c = '1-Daisy';
        insert thirdCampaign;

        Opportunity opportunity = rC_GSATests.initializeOpportunity(account,campaign,false);
        opportunity.Name = 'OpporOne';
        insert opportunity;


        Contact contact = rC_GSATests.initializeContact(account, false);
        contact.LastName = 'Himanshu';
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


        CampaignMember campaignMember = rC_GSATests.initializeCampaignMember(contact.Id, campaign.Id, false);
        campaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        campaignMember.Display_Renewal__c = true;
        campaignMember.Assignment_Type__c = 'Volunteer';
        insert campaignMember;

        CampaignMember secondCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, secondCampaign.Id, false);
        secondCampaignMember.Active__c = true;
        campaignMember.Membership__c = opportunity.Id;
        secondCampaignMember.Display_Renewal__c = true;
        secondCampaignMember.Assignment_Type__c = 'Volunteer';
        insert secondCampaignMember;

        CampaignMember thirdCampaignMember = rC_GSATests.initializeCampaignMember(contact.Id, thirdCampaign.Id, false);
        thirdCampaignMember.Active__c = true;
        thirdCampaignMember.Membership__c = opportunity.Id;
        thirdCampaignMember.Display_Renewal__c = true;
        thirdCampaignMember.Assignment_Type__c = 'Volunteer';
        insert thirdCampaignMember;

        test.startTest();
        system.runAs(portalUser) {
        boolean originalValue;
        rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
        objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
        originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
        objCampaignMemberSetting.rC_Event__Disable_All__c = true;
        upsert objCampaignMemberSetting;

        VolunteerRenewal_RenewalController objVolunteerRenewalController = new VolunteerRenewal_RenewalController();
        objVolunteerRenewalController.displayCampaignMember();
        objVolunteerRenewalController.volunteerPositionRenewalList[0].continuePosition = 'No';
        objVolunteerRenewalController.volunteerPositionRenewalList[1].continuePosition = 'No';
        objVolunteerRenewalController.volunteerPositionRenewalList[2].continuePosition = 'No';
        objVolunteerRenewalController.isInterestedInSearchingForOtherRole = 'No';
        objVolunteerRenewalController.isWantToBeAnAdultMember = 'No';
        Pagereference pageReference = objVolunteerRenewalController.renewPosition();
        List<CampaignMember> campignMembers = VolunteerRegistrationUtilty.campaignMembers(objVolunteerRenewalController.volunteerPositionRenewalList[0].contactName, objVolunteerRenewalController.volunteerPositionRenewalList[0].positon);
        system.assertEquals(campignMembers[0].Display_Renewal__c,false);
        system.assert(string.valueOf(pageReference.getUrl()).contains('VolunteerRenewal_ThankYou'));

        objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
        update objCampaignMemberSetting;
        test.stopTest();
        }
    }

    @isTest(SeeAllData=true)
    static void rc_test_141_CreateAdultMemberSubCampaignTest() {

        Campaign[] parentCampaignList = new Campaign[]{};
        Campaign[] childCampaignList = new Campaign[]{};
        set<Id> parentCampaignIdSet = new set<Id>();
        String RT_VOLUNTEER_JOB_ID = rC_GSATests.getCampaignRecordTypeId('Volunteer Jobs');

        Campaign campaign1 = new Campaign();
        campaign1.Name = 'Test_GSA1';
        campaign1.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Project');
        campaign1.Participation__c='Troop';
        campaign1.CampaignMemberRecordTypeId = RT_ADULT_MEMBER_ID;
        campaign1.Program_Grade_Level__c = '1-Daisy';
        parentCampaignList.add(campaign1);

        Campaign campaign = new Campaign();
        campaign.Name = 'Test_GSA2';
        campaign.RecordTypeId = rC_GSATests.getCampaignRecordTypeId('Volunteer Project');
        campaign.Participation__c='Troop';
        campaign.Program_Grade_Level__c = '1-Daisy';
        campaign.CampaignMemberRecordTypeId = RT_ADULT_MEMBER_ID;
        parentCampaignList.add(campaign);

        test.startTest();

        insert parentCampaignList;
        for(Campaign cmp : parentCampaignList)
            parentCampaignIdSet.add(cmp.id);

        childCampaignList=[select Id from Campaign where IsActive=true AND RecordTypeId=:RT_VOLUNTEER_JOB_ID
                           AND Name='Adult Members' AND ParentId in:parentCampaignIdSet];

        system.assertEquals(parentCampaignList.size(),childCampaignList.size());
        test.stopTest();
    }

}