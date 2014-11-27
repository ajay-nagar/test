public with sharing class Girl_BasicMembershipInfoController extends SobjectExtension{
    public String firstName { get; set; }
    public String lastName { get; set; }
    public String schoolAttend { get; set; }
    public String gradeFall { get; set; }
    public String parentFirstName { get; set; }
    public String parentSecondName { get; set; }
    public String parentEmail { get; set; }
    public String emailConfirm { get; set; }
    public String phone { get; set; }
    public String zipCode { get; set; }
    public String heardAboutUs { get; set; }
    public boolean visibleAboutUs{ get; set; }
    public String eventCode { get; set; }
    public String gradeFallValue { get; set; }
    public String girlSchoolAttending {get; set;}
    public String schoolAttendedId;

    public Integer countTemp { get; set; }

    public static boolean isEventVisible { get; set; }
    public static boolean isCouncilHeaderAvailable { get; set; }
    public static Account councilAccount { get; set; }

    public static final String COUNCIL_RECORDTYPE = 'Council';
    public static final String HOUSEHOLD_RECORDTYPE = 'Household';
    public static final String STANDARD_CAMPAIGN_RECORDTYPE = 'Standard Campaign';
    public final Contact contact;

    public map<String, Schema.SObjectField>CONTACT_FIELD_MAP;
    public set<Contact> contactsToUpdateSet;

    private static final map<String, Schema.RecordTypeInfo> CAMPAIGN_RECORDTYPE_INFO_MAP =  Campaign.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final map<String, Schema.RecordTypeInfo> ACCOUNT_RECORDTYPE_INFO_MAP =  Account.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final map<String, Schema.RecordTypeInfo> CONTACT_RECORDTYPE_INFO_MAP =  Contact.SObjectType.getDescribe().getRecordTypeInfosByName();
    public static String getCampaignRecordTypeId(String name) {
       return (CAMPAIGN_RECORDTYPE_INFO_MAP.get(name) != null) ? CAMPAIGN_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }
    private Zip_Code__c matchingZipCode = new Zip_Code__c();
    private User staticAccountOwnerUser;

    private static String getAccountRecordTypeId(String name) {
       return (ACCOUNT_RECORDTYPE_INFO_MAP.get(name) != null) ? ACCOUNT_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }

    public static final String RT_COUNCIL_ID = getAccountRecordTypeId(COUNCIL_RECORDTYPE);

    public Girl_BasicMembershipInfoController(ApexPages.StandardController stdController) {
        this.contact = (Contact)stdController.getRecord();
        //schoolAttendedId = this.contact.School_Attending__c;
        isEventVisible = false;
        visibleAboutUs = false;
        countTemp = 0;
        contactsToUpdateSet = new set<Contact>();
        //heardAboutUs = null;
        isCouncilHeaderAvailable = false;
    }

    public Girl_BasicMembershipInfoController() {
    }

    public void  eventCodeBlank() {
      eventCode = '';
        visibleAboutUs = true;
        isEventVisible = true;
        countTemp = 1;
        
    }

    public List<SelectOption> getGradeFallItems() {

        list<Selectoption> picklistBlankOptions = new list<Selectoption>();
        CONTACT_FIELD_MAP = contact.getSobjectType().getDescribe().fields.getMap();
        list<Schema.Picklistentry> picklistValuesList = CONTACT_FIELD_MAP.get('Grade__c').getdescribe().getPickListValues();
        if (picklistValuesList.isEmpty())
           return picklistBlankOptions;

        picklistBlankOptions.add(new Selectoption('--None--', '--None--'));
        for(Schema.Picklistentry picklistValues : picklistValuesList) {
           picklistBlankOptions.add(new Selectoption(picklistValues.getLabel(), picklistValues.getValue()));
        }
        return picklistBlankOptions;
    }

    public List<SelectOption> getItems() {
        list<Selectoption> picklistBlankOptions = new list<Selectoption>();
        CONTACT_FIELD_MAP = contact.getSobjectType().getDescribe().fields.getMap();
        list<Schema.Picklistentry> picklistValuesList = CONTACT_FIELD_MAP.get('How_did_you_hear_about_us__c').getdescribe().getPickListValues();

        if (picklistValuesList.isEmpty())
            return picklistBlankOptions;

        picklistBlankOptions.add(new Selectoption('--None--', '--None--'));
        for(Schema.Picklistentry picklistValues : picklistValuesList) {
            picklistBlankOptions.add(new Selectoption(picklistValues.getLabel(), picklistValues.getValue()));
        }
        return picklistBlankOptions;
    }

    public Pagereference submit() {
        Savepoint savepoint = Database.setSavepoint();

        Account matchingAccount;
        Contact matchingContact;
        Contact matchingGirlContact;
        Lead matchingParentLead;
        Lead matchingChildLead;
        Campaign matchingCampaign;

        try {
            String standardCampaignRecTypeId = getCampaignRecordTypeId(STANDARD_CAMPAIGN_RECORDTYPE);
            system.debug('zipCode===>'+zipCode);
            staticAccountOwnerUser = GirlRegistrationUtilty.getSystemAdminUser();

            if(girlSchoolAttending != null){
                List<Account> schoolAccountList = [
                        Select Id
                            , Name
                         from Account
                        Where School_Name_for_Search__c = :girlSchoolAttending
                          and RecordTypeId = :GirlRegistrationUtilty.getAccountRecordTypeId(GirlRegistrationUtilty.SCHOOL_RECORDTYPE)
                ];

                schoolAttendedId = (schoolAccountList != null && schoolAccountList.size() > 0) ? schoolAccountList[0].Id : null;

                if(schoolAttendedId == null)
                {
                    visibleAboutUs=false;                     //change fot GSA-904 on 10-Jun-2014
                     return addErrorMessageAndRollback(savepoint,'Please enter a valid school or choose "School Not Found".');
                }
            }

            String zipCodeToMatch = (zipCode != null && zipCode.length() > 5) ? zipCode.substring(0, 5) + '%' : zipCode + '%';
            Zip_Code__c[] zipCodeList;
            if(zipCodeToMatch != null && zipCodeToMatch != '') {
                zipCodeList = [
                    Select Id
                         , Name
                         , Council__c
                         , Council__r.RecordTypeId
                         , Recruiter__c
                         , Recruiter__r.UserRoleId
                         , Recruiter__r.IsActive
                         , Zip_Code_Unique__c
                         , Owner.Name
                         , City__c
                      From Zip_Code__c
                     Where Zip_Code_Unique__c like:zipCodeToMatch limit 1
                ];
                system.debug('zipCodeList===>'+zipCodeList);
                if (zipCodeList <> NULL && zipCodeList.size() > 0) {
                    Zip_Code__c existingZipCode = zipCodeList[0];
                    
                    if(existingZipCode <> null && existingZipCode.Owner.Name == Label.ZipCodeOwner) {
                        visibleAboutUs=false;
                         zipCode = '';
                        return addErrorMessageAndRollback(savepoint,'This council has not been enabled for the new registration/renewal system. Please visit www.girlscouts.org.'); 
                    }
                    
                    if(existingZipCode.Council__c != null && existingZipCode.Council__r.RecordTypeId != null &&
                       string.valueOf(existingZipCode.Council__r.RecordTypeId).substring(0, 15) == RT_COUNCIL_ID.substring(0, 15)) {
                        councilAccount = getCouncilAccount(zipCodeList[0].Council__c);
                        matchingZipCode = zipCodeList[0];
                    }
                    else {
                        system.debug('zipCodeList===>'+zipCodeList);
                        visibleAboutUs=false;                        //change fot GSA-904 on 10-Jun-2014
                         return addErrorMessageAndRollback(savepoint,'Please check and re enter zip code.  If this is a new zip code, please enter a nearby zip code.');
                    }
                } else {
                    system.debug('zipCodeList===>'+zipCodeList);
                    visibleAboutUs=false;                            //change fot GSA-904 on 10-Jun-2014
                     return addErrorMessageAndRollback(savepoint,'Please check and re enter zip code.  If this is a new zip code, please enter a nearby zip code.');
                }
            }

            matchingContact = getMatchingContact(parentFirstName, parentSecondName, parentEmail, 'Adult');
            matchingGirlContact = getMatchingContact(firstName, lastName, parentEmail, 'Girl');

            system.debug('1. matchingContact =======: ' + matchingContact);
            system.debug('1. matchingGirlContact ===: ' + matchingGirlContact );

            if (matchingContact == null && matchingGirlContact == null) {

                matchingParentLead = getMatchingLead(parentFirstName, parentSecondName, parentEmail);
                if (matchingParentLead != null) {
                    matchingContact = convertLead(matchingParentLead, phone, parentEmail);

                    system.debug('2. matchingContact =======: ' + matchingContact);

                    if(matchingContact != null)
                        matchingGirlContact = createChildContact(matchingContact.AccountId, schoolAttendedId);
                    system.debug('2. matchingGirlContact =======: ' + matchingGirlContact);

                    matchingContact = updateGirlNameOnAdultContact(matchingContact, matchingGirlContact.FirstName);
                    system.debug('###### matchingContact #########' + matchingContact);
                }
                else {
                    matchingAccount = createAccount();

                    if(matchingAccount != null && matchingAccount.Id != null) {
                        matchingContact = createNewParentContact(matchingAccount.Id);

                        if(contact != null && schoolAttendedId != null && matchingAccount.Id != null){
                            matchingGirlContact = createChildContact(matchingAccount.Id, schoolAttendedId);
                        }

                        matchingContact = updateGirlNameOnAdultContact(matchingContact, matchingGirlContact.FirstName);
                    }
                    system.debug('3. matchingContact =======: ' + matchingContact);
                    system.debug('3. matchingGirlContact ===: ' + matchingGirlContact );
                }
            }
            else {
                if (matchingContact <> null && matchingGirlContact <> null) {

                    system.debug('matchingContact =======: ' + matchingContact);
                    system.debug('matchingGirlContact ===: ' + matchingGirlContact );

                    if (matchingContact.AccountId == matchingGirlContact.AccountId) {
                        matchingContact = updateExistingContact(matchingContact);
                        matchingAccount = updateExistingAccount(matchingContact);

                        if(matchingAccount != null)
                            matchingGirlContact = updateExistingGirlContact(matchingGirlContact, schoolAttendedId, matchingAccount.Id);

                        matchingContact = updateGirlNameOnAdultContact(matchingContact, matchingGirlContact.FirstName);

                    } else {
                        matchingContact = updateExistingContact(matchingContact);
                        matchingAccount = updateExistingAccount(matchingContact);

                        if(matchingAccount != null)
                            matchingGirlContact = updateExistingGirlContact(matchingGirlContact, schoolAttendedId, matchingAccount.Id);

                         matchingContact = updateGirlNameOnAdultContact(matchingContact, matchingGirlContact.FirstName);
                    }
                }
                else {
                    if (matchingContact == null || matchingGirlContact == null) {

                        if (matchingContact == null) {
                           matchingAccount = createAccount();
                           if(matchingAccount != null && matchingAccount.Id != null) {
                               matchingContact = createNewParentContact(matchingAccount.Id);
                               matchingGirlContact = updateExistingGirlContact(matchingGirlContact, schoolAttendedId, matchingAccount.Id);

                                matchingContact = updateGirlNameOnAdultContact(matchingContact, matchingGirlContact.FirstName);
                           }

                        } else {
                            matchingAccount = updateExistingAccount(matchingContact);
                            matchingContact = updateExistingContact(matchingContact);

                            if(matchingContact != null)
                                matchingGirlContact = createAssociatedChildContact(matchingContact.AccountId, schoolAttendedId);

                            matchingContact = updateGirlNameOnAdultContact(matchingContact, matchingGirlContact.FirstName);
                        }
                    }
                }
            }
            system.debug('1111111');
            
            if(eventCode != null && eventCode != '') {
                matchingCampaign = getMatchingTroopOrGroup(eventCode);
            }
            else if (eventCode == null || eventCode == '') {
                if(heardAboutUs == null || heardAboutUs == '' || heardAboutUs.toUpperCase().contains('NONE')) {
                     return addErrorMessageAndRollback(savepoint,'Please either provide an event code or select How did you hear about us ?');
                     visibleAboutUs = false;
                }
            }

            if (matchingCampaign != null && matchingContact != null && matchingCampaign.Id != null && matchingContact.Id != null && matchingGirlContact != null && matchingGirlContact.Id != null) {
                if (matchingCampaign.RecordTypeId == standardCampaignRecTypeId) {
                    CampaignMember campaignMember = GirlRegistrationUtilty.createCampaignMember(matchingCampaign, matchingContact, 'true', 0);
                }else {
                    CampaignMember campaignMember = GirlRegistrationUtilty.createCampaignMember(matchingCampaign, matchingContact, 'false', 0);
                } 
                //CampaignMember campaignMember = GirlRegistrationUtilty.createCampaignMember(matchingCampaign, matchingGirlContact, 'null', 0);
            }
            
            if (zipCodeList <> NULL && zipCodeList.size() > 0 && councilAccount <> NULL) {
system.debug('222222');
                if(matchingGirlContact != null && matchingGirlContact.Id != null && matchingContact != null && matchingContact.Id != null && councilAccount != null && councilAccount.Id != null )
                    GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Girl_TroopOrGroupRoleSearch' + '?ParentContactId='+matchingContact.Id + '&CouncilId='+councilAccount.Id+'&GirlContactId='+matchingGirlContact.Id, matchingContact);

                //PageReference troopOrGroupSearch = new PageReference('/apex/Girl_TroopOrGroupRoleSearch');
                PageReference troopOrGroupSearch = Page.Girl_TroopOrGroupRoleSearch;
                if(matchingContact != null && matchingContact.Id != null)
                    troopOrGroupSearch.getParameters().put('ParentContactId', matchingContact.Id);
                if(matchingGirlContact != null && matchingGirlContact.Id != null)
                    troopOrGroupSearch.getParameters().put('GirlContactId', matchingGirlContact.Id);
                if(councilAccount != null && councilAccount.Id != null)
                    troopOrGroupSearch.getParameters().put('CouncilId', councilAccount.Id);
                troopOrGroupSearch.setRedirect(true);
system.debug('matchingGirlContact@@@@@@@@@@@@@@@@'+matchingGirlContact);
system.debug('matchingContact@@@@@@@@@@@@@@@@'+matchingContact);
                return troopOrGroupSearch;

            } else {
system.debug('33333');
                String updateSiteUrlForGirl = '/Girl_TroopOrGroupRoleSearch';

                if(matchingContact != null && matchingContact.Id != null) {
                    updateSiteUrlForGirl = (updateSiteUrlForGirl != null && updateSiteUrlForGirl != '' && !updateSiteUrlForGirl.contains('Girl_TroopOrGroupRoleSearch?'))
                                           ? updateSiteUrlForGirl + '?ParentContactId=' + matchingContact.Id
                                           : updateSiteUrlForGirl + '&ParentContactId=' + matchingContact.Id;
                }

                if(matchingGirlContact != null && matchingGirlContact.Id != null) {
                    updateSiteUrlForGirl = (updateSiteUrlForGirl != null && updateSiteUrlForGirl != '' && !updateSiteUrlForGirl.contains('Girl_TroopOrGroupRoleSearch?'))
                                           ? updateSiteUrlForGirl + '?GirlContactId=' + matchingGirlContact.Id
                                           : updateSiteUrlForGirl + '&GirlContactId=' + matchingGirlContact.Id;
                }

                GirlRegistrationUtilty.updateSiteURLAndContactForGirl(updateSiteUrlForGirl, matchingContact);

                //PageReference troopOrGroupSearch = new PageReference('/apex/Girl_TroopOrGroupRoleSearch');
                PageReference troopOrGroupSearch = Page.Girl_TroopOrGroupRoleSearch;

                if(matchingContact != null && matchingContact.Id != null)
                    troopOrGroupSearch.getParameters().put('ParentContactId', matchingContact.Id);

                if(matchingGirlContact != null && matchingGirlContact.Id != null)
                    troopOrGroupSearch.getParameters().put('GirlContactId', matchingGirlContact.Id);

                troopOrGroupSearch.setRedirect(true);
                return troopOrGroupSearch;
            }
        } catch(System.exception pException) {
            system.debug('4444'+pException);
            visibleAboutUs = false;
            return addErrorMessageAndRollback(savepoint, pException);
        }
        system.debug('555');
        return null;
    }

    public Account getAccount(Id accountId) {
        system.debug('getAccount accountId ===:  ' + accountId);
        Account[] accountList = [
            Select Id
                 , Name
                 , RecordTypeId
                 , ParentId
                 , Instructions__c
            From Account
            Where Id = :accountId limit 1
        ];
        system.debug('getAccount ===:  ' + accountList);

        return (accountList != null && accountList.size() > 0) ? accountList[0] : null;
    }

    public Campaign getMatchingTroopOrGroup(String eventCode) {

        List<Campaign> campaignList = [
            Select Zip_Code__c
                 , Participation__c
                 , ParentId
                 , Id
                 , Event_Code__c
                 , Account__c
                 , RecordTypeId
              From Campaign
             where Event_Code__c = :eventCode limit 1
        ];

        return (campaignList != null && campaignList.size() > 0) ? campaignList[0] : null;
    }

    public Contact getContact(Id contactId) {
        Contact[] contactList = [
            Select Id
                 , Name
                 , RecordTypeId
                 , rC_Bios__Secondary_Contact__c
                 , Secondary_Role__c
              From Contact
             where Id = :contactId limit 1
        ];

        return (contactList != null && contactList.size() > 0) ? contactList[0] : null;
    }

    public Contact getMatchingContact(String firstName, String lastName, String email, String strRole) {
       system.debug('*****firstName====*********'+firstName);
        system.debug('*****lastName====*********'+lastName);
         system.debug('*****email====*********'+email);
          system.debug('*****strRole====*********'+strRole);
          firstName =firstName.Trim();
          lastName=lastName.Trim();
          email=email.Trim();
        List<Contact> contactList = [
            Select Id
                 , FirstName
                 , LastName
                 , Email
                 , Phone
                 , AccountId
                 , Account.RecordType.Name
                 , rC_Bios__Secondary_Contact__c
                 , Secondary_Role__c
              From Contact
             where FirstName = :firstName
               And LastName = :lastName
               And Email = :email
               And rC_Bios__Role__c = :strRole
               And Account.RecordType.Name = :HOUSEHOLD_RECORDTYPE
          order by CreatedDate asc
        ];
        system.debug('*****contactList====*********'+contactList);
        return (contactList != null && contactList.size() > 0) ? contactList[0] : null;
    }

    public Account getCouncilAccount(Id accountId) {

        String CouncilRecTypeId = GirlRegistrationUtilty.getAccountRecordTypeId(COUNCIL_RECORDTYPE) != null
                                  ? GirlRegistrationUtilty.getAccountRecordTypeId(COUNCIL_RECORDTYPE).substring(0, 15)
                                  : null;

        Account[] accountList = [
            Select Id
                 , Name
                 , Volunteer_Financial_Aid_Available__c
                 , RecordTypeId
                 , ParentId
                 , Instructions__c
                 , Council_Header__c
                 , Terms_Conditions__c
             From Account
            where Id = :accountId
              and RecordTypeId = :CouncilRecTypeId limit 1
        ];

        return (accountList != null && accountList.size() > 0) ? accountList[0] : null;
    }

    public Lead getMatchingLead(String firstName, String lastName, String parentEmail) {
        List<Lead> leadList = [
            Select Id
                 , FirstName
                 , LastName
                 , Email
                 , Name
              From Lead
             where FirstName =: firstName
               and LastName =: lastName
               and Email = :parentEmail
               and Lead.IsConverted = false
          order by CreatedDate asc
        ];

        return (leadList != null && leadList.size() > 0) ? leadList[0] : null;
    }

    public User getAccountOwnerUser() {
        List<User> userList = [
            Select Id
                 , LastName
                 , IsActive
                 , Profile.Name
                 , Profile.Id
                 , ProfileId
              From User
             where Name = 'Lori Pender'
               and IsActive = true
               and UserRoleId != null
             limit 1
        ];
        return (userList != null && userList.size() >0) ? userList[0] : new User();
    }

    public Account createAccount() {
        String accountHouseRecordTypeId = GirlRegistrationUtilty.getAccountRecordTypeId(HOUSEHOLD_RECORDTYPE);
        Account account = new Account();
        account.Name = parentFirstName + ' ' + parentSecondName;
        account.RecordTypeId =  accountHouseRecordTypeId;
        account.Phone = phone;
        account.BillingPostalCode = zipCode;
        system.debug('matchingZipCode.Recruiter__r.IsActive========>'+matchingZipCode.Recruiter__r.IsActive);

        if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
            account = GirlRegistrationUtilty.upsertAccountOwner(account, matchingZipCode.Recruiter__c);
        else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
            account = GirlRegistrationUtilty.upsertAccountOwner(account, staticAccountOwnerUser.Id);

        return account != null ? account : null;
    }

    public Contact createNewParentContact(Id accountId) {
        String contactHouseRecordTypeId = GirlRegistrationUtilty.getContactRecordTypeId(HOUSEHOLD_RECORDTYPE);
        Contact contact = new Contact();
        contact.FirstName = parentFirstName;
        contact.LastName = parentSecondName;
        contact.RecordTypeId = contactHouseRecordTypeId;
        contact.AccountId = accountId;
        contact.rC_Bios__Home_Email__c = parentEmail;
        contact.HomePhone = phone;
        contact.MailingPostalCode = zipCode;
        contact.rC_Bios__Preferred_Phone__c = 'Home';
        contact.rC_Bios__Preferred_Email__c = 'Home';
        contact.How_did_you_hear_about_us__c = heardAboutUs;
        contact.Grade__c = gradeFallValue;
        contact.rC_Bios__Role__c = 'Adult';
        contact.rC_Bios__Preferred_Contact__c = true;
        if(contact.Secondary_Role__c != 'Volunteer')
        contact.Welcome_Complete__c = true;
        contact.Secondary_Role__c = 'Parent';
        contact.Get_Started_Complete__c = true;

        if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
            contact = GirlRegistrationUtilty.upsertContactOwner(contact, matchingZipCode.Recruiter__c);
        else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
            contact = GirlRegistrationUtilty.upsertContactOwner(contact, staticAccountOwnerUser.Id);

        return contact;
    }

    public Contact createChildContact(Id accountId,Id SchoolAttendedID ) {
        String contactHouseRecordTypeId = GirlRegistrationUtilty.getContactRecordTypeId(HOUSEHOLD_RECORDTYPE);

        if(accountId != null) {
            Contact[] contactListOfRelatedAccount = [select Id, rC_Bios__Secondary_Contact__c from Contact where AccountId = :accountId];

            List<Contact> contactsToUpdate = new List<Contact>();
            for(Contact associatedContact : contactListOfRelatedAccount) {
                system.debug(' 5 contact.rC_Bios__Secondary_Contact__c ====:  ' + contact.rC_Bios__Secondary_Contact__c);
                if(associatedContact.rC_Bios__Secondary_Contact__c == true) {
                    associatedContact.rC_Bios__Secondary_Contact__c = false;
                    contactsToUpdate.add(associatedContact);
                }
            }
            system.debug(' contactsToUpdate ====:  ' + contactsToUpdate);
            update contactsToUpdate;
        }

        Contact contact = new Contact();
        contact.FirstName = firstName;
        contact.LastName = lastName;
        contact.RecordTypeId = contactHouseRecordTypeId;
        contact.AccountId = accountId;
        contact.rC_Bios__Home_Email__c = parentEmail;
        contact.HomePhone = phone;
        contact.MailingPostalCode = zipCode;
        contact.rC_Bios__Preferred_Phone__c = 'Home';
        contact.rC_Bios__Preferred_Email__c = 'Home';
        contact.How_did_you_hear_about_us__c = heardAboutUs;
        contact.Grade__c = gradeFallValue;
        contact.rC_Bios__Role__c = 'Girl';
        contact.School_Attending__c = SchoolAttendedID;
        contact.rC_Bios__Secondary_Contact__c = true;
        system.debug(' 5 accountId ====:  ' + accountId);

        contact.rC_Bios__Secondary_Contact__c = true;

        if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
           contact = GirlRegistrationUtilty.upsertContactOwner(contact, matchingZipCode.Recruiter__c);
        else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
           contact = GirlRegistrationUtilty.upsertContactOwner(contact, staticAccountOwnerUser.Id);

        return contact;
    }

    public Contact createAssociatedChildContact(Id accountId,Id SchoolAttendedID ) {

        String contactHouseRecordTypeId = GirlRegistrationUtilty.getContactRecordTypeId(HOUSEHOLD_RECORDTYPE);

        if(accountId != null) {
            Contact[] contactListOfRelatedAccount = [select Id, rC_Bios__Secondary_Contact__c from Contact where AccountId = :accountId];
            List<Contact> contactsToUpdate = new List<Contact>();
            for(Contact associatedContact : contactListOfRelatedAccount ) {
                if(associatedContact.rC_Bios__Secondary_Contact__c == true) {
                    associatedContact.rC_Bios__Secondary_Contact__c = false;
                    contactsToUpdate.add(associatedContact);
                }
            }
            if(contactsToUpdate != null && contactsToUpdate.size() > 0)
                update contactsToUpdate;
        }
        system.debug('==== Contacts : ' + [select Id, rC_Bios__Secondary_Contact__c from Contact where AccountId = :accountId]);
        Contact contact = new Contact();
        contact.FirstName = firstName != null ? firstName : '';
        contact.LastName = lastName != null ? lastName : '';
        contact.RecordTypeId = contactHouseRecordTypeId;
        contact.AccountId = accountId;
        contact.rC_Bios__Home_Email__c = parentEmail != null ? parentEmail : '';
        contact.HomePhone = phone != null ? phone : '';
        contact.MailingPostalCode = zipCode != null ? zipCode : '';
        contact.rC_Bios__Preferred_Phone__c = 'Home';
        contact.rC_Bios__Preferred_Email__c = 'Home';
        contact.How_did_you_hear_about_us__c = heardAboutUs != null ? heardAboutUs : '';
        contact.Grade__c = (gradeFallValue != null && !gradeFallValue.toUpperCase().contains('NONE')) ? gradeFallValue : '';
        contact.rC_Bios__Role__c = 'Girl';
        contact.School_Attending__c = SchoolAttendedID;
        contact.rC_Bios__Secondary_Contact__c = true;

        if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
            contact = GirlRegistrationUtilty.upsertContactOwner(contact, matchingZipCode.Recruiter__c);
        else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
            contact = GirlRegistrationUtilty.upsertContactOwner(contact, staticAccountOwnerUser.Id);

        return contact;
    }

    public Contact convertLead(Lead leadToConvert, String phone, String parentEmail) {

        String AccountHouseholdRecTypeId = GirlRegistrationUtilty.getAccountRecordTypeId(HOUSEHOLD_RECORDTYPE);
        String ContactHouseholdRecTypeId = GirlRegistrationUtilty.getContactRecordTypeId(HOUSEHOLD_RECORDTYPE);
        Database.LeadConvert lc = new Database.LeadConvert();
        lc.setLeadId(leadToConvert.id);
        lc.setDoNotCreateOpportunity(true);

        LeadStatus convertStatus = [SELECT Id, MasterLabel FROM LeadStatus WHERE IsConverted=true LIMIT 1];
        lc.setConvertedStatus(convertStatus.MasterLabel);

        Database.LeadConvertResult lcr = Database.convertLead(lc);

        system.debug('convertLead == lcr ====: ' + lcr);
        system.debug('convertLead == lcr.getAccountId() ====: ' + lcr.getAccountId());

        //Account account = getAccount(lcr.getAccountId());
        Account account = new Account(Id = lcr.getAccountId());
        system.debug('convertLead == account ====: ' + account);

        if (account != null) {
            system.debug('convertLead 1 == phone ==: ' + phone + ' == zipCode == ' + zipCode);
            account.Phone = phone;
            account.BillingPostalCode = zipCode;
            system.debug('convertLead 1 == account.RecordTypeId ==: ' + account.RecordTypeId + ' == AccountHouseholdRecTypeId == ' + AccountHouseholdRecTypeId);

            if (account.RecordTypeId == null || (account.RecordTypeId != null && String.valueOf(account.RecordTypeId).substring(0, 15) != AccountHouseholdRecTypeId))
                account.RecordTypeId = AccountHouseholdRecTypeId;

            system.debug('convertLead 1 == account ====: ' + account);

            system.debug('convertLead == matchingZipCode ====: ' + matchingZipCode);

            if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
                account = GirlRegistrationUtilty.upsertAccountOwner(account, matchingZipCode.Recruiter__c);
            else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
                account = GirlRegistrationUtilty.upsertAccountOwner(account, staticAccountOwnerUser.Id);
        }

        Contact contact = getContact(lcr.getContactId());

        system.debug('convertLead == contact ====: ' + contact);

        if (contact != null) {
            contact.rC_Bios__Home_Email__c = parentEmail;
            contact.HomePhone = phone;
            contact.rC_Bios__Preferred_Phone__c = 'Home';
            contact.rC_Bios__Preferred_Email__c = 'Home';
            contact.MailingPostalCode = zipCode;
            contact.How_did_you_hear_about_us__c = (heardAboutUs != null) ? heardAboutUs : '';
            contact.rC_Bios__Role__c = 'Adult';
            if(contact.Secondary_Role__c != 'Volunteer')
            contact.Welcome_Complete__c = true;
            contact.Secondary_Role__c = 'Parent';
            if(account != null && account.Id != null)
                contact.AccountId = account.id;

            contact.Get_Started_Complete__c = true;

            //contact.rC_Bios__Secondary_Contact__c = false;

            if (String.valueOf(contact.RecordTypeId).substring(0, 15) != ContactHouseholdRecTypeId)
                contact.RecordTypeId = ContactHouseholdRecTypeId;

            if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
                contact = GirlRegistrationUtilty.upsertContactOwner(contact, matchingZipCode.Recruiter__c);
            else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
                contact = GirlRegistrationUtilty.upsertContactOwner(contact, staticAccountOwnerUser.Id);
        }
        system.debug('convertLead == contact.rC_Bios__Secondary_Contact__c ====: ' + contact.rC_Bios__Secondary_Contact__c);
        return contact;
    }

    public Contact updateExistingContact(Contact contact) {

        if(contact != null && contact.Id != null) {
            contact.rC_Bios__Preferred_Email__c = 'Home';
            contact.rC_Bios__Preferred_Phone__c = 'Home';
            contact.HomePhone = phone;
            contact.rC_Bios__Home_Email__c = parentEmail;
            contact.MailingPostalCode = zipCode;
            contact.Grade__c = gradeFallValue;
            contact.How_did_you_hear_about_us__c = heardAboutUs;
            contact.rC_Bios__Role__c = 'Adult';

            contact.Get_Started_Complete__c = true;
            if(contact.Secondary_Role__c != 'Volunteer')
            contact.Welcome_Complete__c = true;
            contact.Secondary_Role__c = 'Parent';
             if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
                contact = GirlRegistrationUtilty.upsertContactOwner(contact, matchingZipCode.Recruiter__c);
             else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
                contact = GirlRegistrationUtilty.upsertContactOwner(contact, staticAccountOwnerUser.Id);
        }

        return contact;
    }

    public Contact updateExistingGirlContact(Contact matchingGirlContact, Id SchoolAttendedID, String accountId) {

        Contact[] contactsToUpdateList;

        if(accountId != null) {
            if(matchingGirlContact != null) {
                contactsToUpdateList = [
                    select Id
                         , Name
                         , rC_Bios__Secondary_Contact__c
                      from Contact
                     where AccountId = : accountId
                ];
            }

            if(contactsToUpdateList != null && contactsToUpdateList.size() > 0) {
                for(Contact contactToUpdate : contactsToUpdateList)
                    contactToUpdate.rC_Bios__Secondary_Contact__c = false;

                update contactsToUpdateList;
            }
        }

        if(matchingGirlContact != null && matchingGirlContact.Id != null) {
            matchingGirlContact.rC_Bios__Preferred_Email__c = 'Home';
            matchingGirlContact.rC_Bios__Preferred_Phone__c = 'Home';
            matchingGirlContact.Grade__c = (gradeFallValue != null && !gradeFallValue.toUpperCase().contains('none')) ? gradeFallValue : '';
            matchingGirlContact.HomePhone = phone != NULL ? phone : '';
            matchingGirlContact.rC_Bios__Home_Email__c = parentEmail != NULL ? parentEmail : '';
            matchingGirlContact.MailingPostalCode = zipCode != null ? zipCode : '';
            matchingGirlContact.How_did_you_hear_about_us__c = heardAboutUs != null ? heardAboutUs : '';
            matchingGirlContact.rC_Bios__Role__c = 'Girl';
            matchingGirlContact.School_Attending__c = SchoolAttendedID;
            matchingGirlContact.AccountId = accountId;
            /*
            if(matchingGirlContact != null){
                contactsToUpdateList = [
                    select Id
                         , Name
                         , rC_Bios__Secondary_Contact__c
                      from Contact
                     where AccountId = : accountId
                ];
            }

            if(contactsToUpdateList != null && contactsToUpdateList.size() > 0) {
                for(Contact contactToUpdate : contactsToUpdateList)
                    contactToUpdate.rC_Bios__Secondary_Contact__c = false;

                //database.update(contactsToUpdateList);
            }
            */

            matchingGirlContact.rC_Bios__Secondary_Contact__c = true;

            if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
                matchingGirlContact = GirlRegistrationUtilty.upsertContactOwner(matchingGirlContact, matchingZipCode.Recruiter__c);
            else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
                matchingGirlContact = GirlRegistrationUtilty.upsertContactOwner(matchingGirlContact, staticAccountOwnerUser.Id);

        }
        return matchingGirlContact;
    }

    public Account updateExistingAccount(Contact contact) {

        if (contact != null && contact.AccountId != null) {
            Account[] accountList = [
                Select Id
                     , Phone
                     , rC_Bios__Preferred_Contact_Email__c
                     , rC_Bios__Preferred_Contact_Phone__c
                  From Account
                 Where Id = :contact.AccountId
                 limit 1
            ];
            Account account = (accountList != null && accountList.size() > 0) ? accountList[0] : new Account();

            if (account != null && account.Id != null) {

                account.Phone = phone;
                account.BillingPostalCode = zipCode;

                if (matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.UserRoleId != null && matchingZipCode.Recruiter__r.IsActive)
                    account = GirlRegistrationUtilty.upsertAccountOwner(account, matchingZipCode.Recruiter__c);
                else if(staticAccountOwnerUser != null && staticAccountOwnerUser.Id != null)
                    account = GirlRegistrationUtilty.upsertAccountOwner(account, staticAccountOwnerUser.Id);

                return account;
            }
        }
        return null;
    }

     @RemoteAction
    public static List<String> searchSchools(String strZipCode, String searchText) {
        Savepoint savepoint = Database.setSavepoint();

        system.debug('=====strZipCode=======>'+strZipCode+ '=searchText=======>'+searchText);

        List<String> schoolsNearByList = new List<String>();

        try{
            if(strZipCode != null && searchText != null && searchText != ''){

                String JSONString;
                Integer selectedRadius = 5;
                Zip_Code__c objZipCode = new Zip_Code__c();
                set<String> accountZipCodeSet = new set<String>();
                Map<String, String> zipCodeVsAccountNameMap = new Map<String, String>();
                String accountRecordTypeId = GirlRegistrationUtilty.getAccountRecordTypeId(GirlRegistrationUtilty.SCHOOL_RECORDTYPE);
                //String searchQueri = '';

                if(strZipCode != null)
                objZipCode = VolunteerRegistrationUtilty.getZipCode(strZipCode);
            
            String searchDefaultQuery = 'Select Id, Name from Account Where (Name = '+ '\'Home Schooled\'' + ' Or Name = ' + '\'School Not Found\'' + ') and RecordTypeId = \'' + accountRecordTypeId + '\' order by Name' ;
                List<Account> defaultSchool = database.query(searchDefaultQuery);
                system.debug('=====defaultSchool=======>'+defaultSchool);

                if(defaultSchool != null && defaultSchool.size() > 0)
                    for(Account account : defaultSchool)
                        schoolsNearByList.add(account.Name);
            
            if(objZipCode != null && objZipCode.geo_location__Latitude__s != null && objZipCode.geo_location__Longitude__s != null) {
                
                List<Zip_Code__c> zipCodeList = VolunteerRegistrationUtilty.getAllZipCodeWithingSelectedRadius(String.valueOf(objZipCode.geo_location__Latitude__s), String.valueOf(objZipCode.geo_location__Longitude__s), System.Label.Radius_For_School_Name_Search);
                system.debug('zipCodeList#####'+zipCodeList);
                
                if(!zipCodeList.isEmpty()) {
                    Set<String> zipCodeUniqueSet = new Set<String>();
                    
                    for(Zip_Code__c newZipCode : zipCodeList)
                        zipCodeUniqueSet.add(newZipCode.Zip_Code_Unique__c);
                    
                    system.debug('zipCodeUniqueSet#####'+zipCodeUniqueSet);
                
               String searchQuery = 'Select Id, Name, BillingPostalCode,School_Name_for_Search__c From Account Where Name Like \'%'+searchText+'%\' and RecordTypeId = \'' + accountRecordTypeId + '\' and BillingPostalCode_for_Search__c IN :zipCodeUniqueSet' + ' order by Name limit 100' ; //and BillingPostalCode  != null
               List<Account> accountWithZipCode = database.query(searchQuery);
               if(!accountWithZipCode.isEmpty()) {
                        for(Account account : accountWithZipCode)
                            schoolsNearByList.add(account.School_Name_for_Search__c);
               }
                
                        JSONString = JSON.serialize(schoolsNearByList);
                        system.debug('=====JSONString=======>'+JSONString);
                }
            }
          }  
        } catch(System.exception pException) {
            system.debug('pException.getMessage==>'+pException.getMessage());
        }
        system.debug('=====schoolsNearByList=======>'+schoolsNearByList);
        return schoolsNearByList;
    }

    public static double calcDistance(double latA, double longA, double latB, double longB) {
        double radian = 57.295;
        double theDistance = (Math.sin(latA/radian) * Math.sin(latB/radian) + Math.cos(latA/radian) * Math.cos(latB/radian) * Math.cos((longA - longB)/radian));
        double dis = (Math.acos(theDistance)) * 69.09 * radian;
        return dis;
    }

    public Contact updateGirlNameOnAdultContact(Contact adultContact, String girlFirstName) {
        if(adultContact != null && adultContact.Id != null) {
            adultContact.Girl_Registration__c = true;
            adultContact.Girl_First_Name__c = girlFirstName;
            update adultContact;
        }
        return adultContact;
    }
}