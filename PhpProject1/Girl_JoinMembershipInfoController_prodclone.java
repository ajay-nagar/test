public with sharing class Girl_JoinMembershipInfoController extends SobjectExtension{

    public String membershipProduct                     { get; set; }
    public String dateOfBirth                           { get; set; }
    public String girlPhone                             { get; set; }
    public String girlEmail                             { get; set; }
    public String selectedCustodialInfo                 { get; set; }
    public Boolean custodialFlag                        { get; set; }
    public String councilServiceFeeString               { get; set; }
    public String streetLine1                           { get; set; }
    public String streetLine2                           { get; set; }
    public String city                                  { get; set; }
    public String state                                 { get; set; }
    public String zipCode                               { get; set; }
    public String county                                { get; set; }
    public String country                               { get; set; }
    public boolean emailOptIn                           { get; set; }
    public boolean textOptIn                            { get; set; }
    public String primaryFirstName                      { get; set; }
    public String secondaryFirstName                    { get; set; }
    public String primaryLastName                       { get; set; }
    public String secondaryLastName                     { get; set; }
    public String primaryEmail                          { get; set; }
    public String secondaryEmail                        { get; set; }
    public String primaryEmail2                         { get; set; }
    public String secondaryEmail2                       { get; set; }
    public String primaryPreferredEmail                 { get; set; }
    public String secondaryPreferredEmail               { get; set; }
    public String primaryGender                         { get; set; }
    public String secondaryGender                       { get; set; }
    public String primaryHomePhone                      { get; set; }
    public String secondaryHomePhone                    { get; set; }
    public String primaryBusinessPhone                  { get; set; }
    public String secondaryBusinessPhone                { get; set; }
    public String primaryMobilePhone                    { get; set; }
    public String secondaryMobilePhone                  { get; set; }
    public String primaryPreferredPhone                 { get; set; }
    public String secondaryPreferredPhone               { get; set; }

    public static String parentContactId;
    public static String girlContactId;

    public String girlFirstName                         { get; set; }
    public String girlLastName                          { get; set; }
    public String primaryStreetLine1                    { get; set; }
    public String secondaryStreetLine1                  { get; set; }
    public String primaryStreetLine2                    { get; set; }
    public String secondaryStreetLine2                  { get; set; }
    public String primaryCity                           { get; set; }
    public String secondaryCity                         { get; set; }
    public String primaryState                          { get; set; }
    public String secondaryState                        { get; set; }
    public String primaryZipCode                        { get; set; }
    public String secondaryZipCode                      { get; set; }
    public String primaryCounty                         { get; set; }
    public String secondaryCounty                       { get; set; }
    public String primaryCountry                        { get; set; }
    public String secondaryCountry                      { get; set; }
    public boolean primaryEmailOptIn                    { get; set; }
    public boolean secondaryEmailOptIn                  { get; set; }
    public boolean primaryTextOptIn                     { get; set; }
    public boolean secondaryTextOptIn                   { get; set; }
    public Boolean booleanContactPhotoOptIn             { get; set; }
    public Boolean booleanOppGrantRequested             { get; set; }
    public Boolean booleanGrantRequested                { get; set; }
    public Boolean booleanOpportunityGrantRequested     { get; set; }

    public Account councilAccount                       { get; set; }
    public Boolean isCouncilTermsConditionsAvailable    { get; set; }
    public Boolean booleanContactEmailOptIn             { get; set; }
    public Boolean booleanContactTextPhoneOptIn         { get; set; }
    public Boolean financialCheckBox                    { get; set; }
    public Boolean booleanTermsAndConditions            { get; set; }
    public Boolean booleanOppMembershipOnPaper          { get; set; }
    public Boolean tremsflag                            { get; set; }
    public boolean isGirlAbove13                        { get; set; }

    public String firstName                             { get; set; }
    public String lastName                              { get; set; }
    public String preferredEmail                        { get; set; }
    public String homePhone                             { get; set; }
    public String email                                 { get; set; }
    public String gender                                { get; set; }
    public String preferredPhone                        { get; set; }
    public String mobilePhone                           { get; set; }
    public String termsAndCondition                     { get; set; }

    private Contact contact;
    private Contact parentContact;
    private Contact girlContact;
    private Contact secondaryContact;

    private Account parentAccount;
    private Opportunity councilMembershipOpp;

    private PricebookEntry[] PricebookEntryList;
    private Zip_Code__c matchingZipCode = new Zip_Code__c();
    private String councilId;
    private String contactId;
    private User systemAdminUser;
    private Double councilServiceFee = 0.0;
    private static Integer counterUnableToLockRow = 0;
    private map<Id, PricebookEntry> priceBookEntryMap;

    String errorMessage = '';

    public static final string LIFETIME_MEMBERSHIP          = 'Lifetime Membership';
    public static final string COUNCIL_SERVICE_FEE          = 'Council Service Fee';
    public static final string GIRL_SCOUTS_USA_PRICEBOOK    = 'Girl Scouts USA';

    public Girl_JoinMembershipInfoController() {
        counterUnableToLockRow = 0;
        booleanOppMembershipOnPaper = false;
        booleanOpportunityGrantRequested = false;
        isGirlAbove13 = false;
        tremsflag = false;
        girlPhone = '';
        girlEmail = '';
        custodialFlag = false;
        booleanTermsAndConditions = false;
        booleanContactPhotoOptIn = true;
        booleanOppGrantRequested = false;

        emailOptIn = true;
        primaryEmailOptIn = true;
        secondaryEmailOptIn = true;

        textOptIn = false;

        termsAndCondition = 'I/we accept and abide by the Girl Scout Promise and Law.';
        priceBookEntryMap = new map<Id, PricebookEntry>();
        PricebookEntryList = new PricebookEntry[]{};
        systemAdminUser = GirlRegistrationUtilty.getSystemAdminUser();
        fillPricebookEntryList();

        //if(Apexpages.currentPage().getParameters().containsKey('ParentContactId'))
            parentContactId = Apexpages.currentPage().getParameters().get('ParentContactId');
        //if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            girlContactId = Apexpages.currentPage().getParameters().get('GirlContactId');
        //if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');
        //if(Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            councilAccount = GirlRegistrationUtilty.getCouncilAccount(Apexpages.currentPage().getParameters().get('CouncilId'));

        if(councilAccount != null && councilAccount.Id != null) {
            tremsflag = (councilAccount.Terms_Conditions__c != null) ? true : false;
            isCouncilTermsConditionsAvailable = (councilAccount.Terms_Conditions__c != null && councilAccount.Terms_Conditions__c != '') ? true : false;
            financialCheckBox  = (councilAccount.Volunteer_Financial_Aid_Available__c != null && councilAccount.Volunteer_Financial_Aid_Available__c == true) ? true : false;
        }

        if(councilId != null && councilId != '') {
            Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);
            if(Girl_RegistrationHeaderController.councilAccount != null){
                booleanOppGrantRequested = Girl_RegistrationHeaderController.councilAccount.Girl_Financial_Aid_Available__c;

                if(Girl_RegistrationHeaderController.councilAccount.Service_Fee__c != null)
                    councilServiceFeeString = 'Council Service Fee $' + Girl_RegistrationHeaderController.councilAccount.Service_Fee__c;
                else
                    councilServiceFeeString = 'Council Service Fee $0.00' ;
            }
        }

        if(girlContactId != null && girlContactId != '')
            girlContact = getContact(girlContactId);

        if(girlContact != null && girlContact.Id != null && girlContact.MailingPostalCode != null)
            zipCode = girlContact.MailingPostalCode;

        if(parentContactId != null && parentContactId != '')
            parentContact = getContact(parentContactId);

        if(parentContact != null) {
            primaryFirstName = parentContact.FirstName;
            primaryLastName = parentContact.LastName;
            primaryEmail = parentContact.rC_Bios__Home_Email__c;
            primaryPreferredEmail = (parentContact.rC_Bios__Preferred_Email__c.equalsIgnoreCase('Home'))
                                    ? 'Email'
                                    : (parentContact.rC_Bios__Preferred_Email__c.equalsIgnoreCase('Work')) ? 'Email 2' : '';

            if(parentContact.rC_Bios__Preferred_Phone__c != null && parentContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Home'))
                primaryPreferredPhone = 'Home Phone';
            if(parentContact.rC_Bios__Preferred_Phone__c != null && parentContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Work'))
                primaryPreferredPhone = 'Business Phone';
            if(parentContact.rC_Bios__Preferred_Phone__c != null && parentContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Mobile'))
                primaryPreferredPhone = 'Mobile Phone';

            primaryZipCode = parentContact.MailingPostalCode;
            primaryHomePhone = parentContact.HomePhone;
            primaryBusinessPhone = parentContact.rC_Bios__Work_Phone__c;
            primaryMobilePhone = parentContact.MobilePhone;
            primaryEmail2 = parentContact.rC_Bios__Work_Email__c;
       }

       if(girlContact != null && girlContact.Id != null) {
           girlFirstName = (girlContact.FirstName != null) ? girlContact.FirstName : '';
           girlLastName = (girlContact.LastName != null) ? girlContact.LastName : '';
       }
       secondaryPreferredEmail = 'Email';
       secondaryPreferredPhone = 'Home Phone';
    }

    public pageReference calculateGirlAge() {
        Date birthDate;
        Savepoint savepoint = Database.setSavepoint();
        try {
            system.debug('***** dateOfBirth' + dateOfBirth);
            if(dateOfBirth != null && dateOfBirth != '' && dateOfBirth.contains('/')) {
                String[] strBirthDate = dateOfBirth.trim().split('/');
                birthDate = Date.newInstance(Integer.valueof(strBirthDate[2].trim()), Integer.valueof(strBirthDate[0].trim()), Integer.valueof(strBirthDate[1].trim()));
            }

            if(birthDate != null)
                isGirlAbove13 = math.floor((birthDate.daysBetween(Date.Today()))/365.25) >= 13 ? true : false;
                if(isGirlAbove13 == true) {
                    if(parentContact <> null){
                        girlPhone = (parentContact.HomePhone <> null) ? parentContact.HomePhone : '';
                        girlEmail = (parentContact.rC_Bios__Home_Email__c <> null) ? parentContact.rC_Bios__Home_Email__c : '';
                    }
                }

        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public List<SelectOption> getmembershipProductList() {
       List<SelectOption> membershipProducts = new List<SelectOption>();
       membershipProducts.add(new Selectoption('--None--', '--None--'));

       if(!PricebookEntryList.isEmpty() && PricebookEntryList.size() > 0) {
           for(PricebookEntry pricebookEntry : PricebookEntryList) {
               if(pricebookEntry.Name.toUpperCase().contains('GIRL') || pricebookEntry.Name.toUpperCase().contains('GIRL'))
                   membershipProducts.add(new SelectOption(pricebookEntry.Id, '$'+pricebookEntry.UnitPrice +' ' +pricebookEntry.Name));
           }
       }
       return membershipProducts;
   }

    public void showSecondaryContact() {
        custodialFlag = true;
        if(selectedCustodialInfo.equalsIgnoreCase('Parent'))
            custodialFlag = false;
        if( selectedCustodialInfo.equalsIgnoreCase('Guardian') || selectedCustodialInfo.equalsIgnoreCase('--None--'))
            custodialFlag = false;
    }

    public Contact getContact(String strContactId) {
        Contact[] contactList = [
            Select Id
                 , FirstName
                 , LastName
                 , Birthdate
                 , AccountId
                 , rC_Bios__Home_Email__c
                 , rC_Bios__Gender__c
                 , rC_Bios__Preferred_Email__c
                 , rC_Bios__Preferred_Phone__c
                 , rC_Bios__Work_Phone__c
                 , rC_Bios__Work_Email__c
                 , MailingPostalCode
                 , HomePhone
                 , MobilePhone
              From Contact
             where Id = :strContactId
             limit 1
        ];
        return (contactList != null && contactList.size() > 0) ? contactList[0] : new Contact();
    }

    public Contact updatePrimaryContact(Contact contactToUpdate) {
       if(contactToUpdate != null && contactToUpdate.Id != null) {
            contactToUpdate.FirstName = (primaryFirstName != null && primaryFirstName != '' && primaryFirstName.length() > 40) ? primaryFirstName.substring(0, 40) : primaryFirstName;
            contactToUpdate.LastName  = (primaryLastName  != null && primaryLastName != '' && primaryLastName.length()  > 80) ? primaryLastName.substring(0, 80) : primaryLastName;

            contactToUpdate.rC_Bios__Gender__c = (primaryGender != null && primaryGender != '' && primaryGender.toUpperCase().contains('NONE')) ? '' : primaryGender;
            contactToUpdate.rC_Bios__Preferred_Email__c = (primaryPreferredEmail != null && primaryPreferredEmail != '' && primaryPreferredEmail.toUpperCase().contains('NONE'))
                                                              ? ''
                                                              : (primaryPreferredEmail.equalsIgnoreCase('Email') ? 'Home' : 'Work');
            contactToUpdate.Volunteer_Terms_and_Conditions__c = true;
            contactToUpdate.rC_Bios__Home_Email__c = primaryEmail;
            contactToUpdate.rC_Bios__Work_Email__c = primaryEmail2;

            if(primaryPreferredPhone != null && primaryPreferredPhone != ''){

                if(primaryPreferredPhone.equalsIgnoreCase('Home Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Home';
                    //contactToUpdate.HomePhone = primaryHomePhone;
                }

                if(primaryPreferredPhone.equalsIgnoreCase('Business Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Work';
                    //contactToUpdate.rC_Bios__Work_Phone__c = (primaryBusinessPhone != null && primaryBusinessPhone  != '') ? primaryBusinessPhone : primaryHomePhone;
                }

                if(primaryPreferredPhone.equalsIgnoreCase('Mobile Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Mobile';
                    //contactToUpdate.MobilePhone = (primaryMobilePhone != null && primaryMobilePhone != '') ? primaryMobilePhone : primaryHomePhone;
                }
            }

            contactToUpdate.HomePhone = primaryHomePhone;
            contactToUpdate.MobilePhone = (primaryMobilePhone != null && primaryMobilePhone != '') ? primaryMobilePhone : '';
            contactToUpdate.rC_Bios__Work_Phone__c = (primaryBusinessPhone != null && primaryBusinessPhone  != '') ? primaryBusinessPhone : '';

            contactToUpdate.Email_Opt_In__c = primaryEmailOptIn;
            contactToUpdate.Text_Phone_Opt_In__c = primaryTextOptIn;
            contactToUpdate.Photo_Opt_In__c = booleanContactPhotoOptIn;

            //GSA-1110  :  Make primary contact as a preferred contact on household and make existing preferred to false
            List<Contact> contactsToUpdateList = new List<Contact>();
            system.debug('=== parentAccount ===:  ' + parentAccount);
            if(parentAccount != null && parentAccount.Id != null) {
                List<Contact> preferredContactList = [
                    Select Id
                         , AccountId
                         , rC_Bios__Preferred_Contact__c
                         , Adult_Member__c
                      From Contact
                     where AccountId = :parentAccount.Id
                       and rC_Bios__Preferred_Contact__c = true
                       and Id != :contactToUpdate.Id
                ];

                if(preferredContactList != null && preferredContactList.size() > 0) {
                    for(Contact conToUpdate : preferredContactList) {
                        conToUpdate.rC_Bios__Preferred_Contact__c = false;
                        contactsToUpdateList.add(conToUpdate);
                    }
                    /*
                        update contactsToUpdateList;
                        Needs to resolve : Error:There are multiple contacts on this account marked as preferred
                    */
                    system.debug('== contactsToUpdateList ===:  ' + contactsToUpdateList);
                }
            }

            /*
                contactToUpdate.rC_Bios__Preferred_Contact__c = true;
                Needs to resolve : Error:There are multiple contacts on this account marked as preferred
            */

            system.debug('=== contactToUpdate : ' + contactToUpdate);
            update contactToUpdate;
            return contactToUpdate;
        }
        return null;
    }

    public Contact updateGirlContact(Contact contactToUpdate) {


        if(contactToUpdate != null && contactToUpdate.Id != null) {
            String[] strBirthDate;

            if(dateOfBirth.contains('/')) {
                strBirthDate = dateOfBirth.trim().split('/');
                //birthDate = Date.newInstance(Integer.valueof(strBirthDate[2].trim()), Integer.valueof(strBirthDate[0].trim()), Integer.valueof(strBirthDate[1].trim()));
            }
            system.debug('***selectedCustodialInfo***'+selectedCustodialInfo);
            //contactToUpdate.Birthdate = birthDate;
            contactToUpdate.rC_Bios__Birth_Day__c=strBirthDate[1].trim();
            contactToUpdate.rC_Bios__Birth_Month__c=strBirthDate[0].trim();
            contactToUpdate.rC_Bios__Birth_Year__c=strBirthDate[2].trim();
            if(selectedCustodialInfo <> null && selectedCustodialInfo <> '') {
                contactToUpdate.Custodial_Care__c = selectedCustodialInfo;
            }
            contactToUpdate.rC_Bios__Home_Email__c = (girlEmail != null && girlEmail != '') ? girlEmail : primaryEmail;

            if(girlPhone != null && girlPhone != '')
                contactToUpdate.HomePhone = (girlPhone.length() > 20) ? girlPhone.substring(0, 20) : girlPhone;

            contactToUpdate.rC_Bios__Preferred_Email__c = 'Home';
            contactToUpdate.rC_Bios__Preferred_Phone__c = 'Home';
            contactToUpdate.Email_Opt_In__c = emailOptIn;
            contactToUpdate.Text_Phone_Opt_In__c = textOptIn;
            contactToUpdate.Photo_Opt_In__c = booleanContactPhotoOptIn;

            //update contactToUpdate;
            updateGirlContactInfo(contactToUpdate.Id, dateOfBirth, selectedCustodialInfo, girlEmail, primaryEmail, girlPhone, emailOptIn, textOptIn, primaryHomePhone,booleanContactPhotoOptIn);

            return contactToUpdate;
        }
        return null;
    }

    public Contact createSecondaryContact(String primaryContactId) {
        Contact secondaryContact = new Contact();
        Zip_Code__c[] zipCodeList = new Zip_Code__c[] {};
        Zip_Code__c secondaryZipCodeRecord;

        Contact primaryContact = getContact(primaryContactId);

        if(primaryContact != null && primaryContact.Id != null) {
            system.debug('===inside==>');
            secondaryContact.FirstName = (secondaryFirstName != null && secondaryFirstName  != '' && secondaryFirstName.length() > 40) ? secondaryFirstName.substring(0, 40) : secondaryFirstName;
            secondaryContact.LastName = (secondaryLastName != null && secondaryLastName != '' && secondaryLastName.length() > 80) ? secondaryLastName.substring(0, 80) : secondaryLastName;
            secondaryContact.AccountId = primaryContact.AccountId;
            //secondaryContact.rC_Bios__Home_Email__c = secondaryEmail;
            //secondaryContact.rC_Bios__Work_Email__c = (secondaryEmail2 != null && secondaryEmail2 != '') ? secondaryEmail2 : secondaryEmail;
            secondaryContact.rC_Bios__Gender__c = (secondaryGender != null && secondaryGender != '' && secondaryGender.toUpperCase().contains('NONE')) ? '' : secondaryGender;
            secondaryContact.rC_Bios__Role__c = 'Adult';
            secondaryContact.HomePhone = secondaryHomePhone;
            secondaryContact.Email_Opt_In__c = secondaryEmailOptIn;
            secondaryContact.Text_Phone_Opt_In__c = secondaryTextOptIn;
            secondaryContact.Photo_Opt_In__c = booleanContactPhotoOptIn;
            system.debug('=11====>'+secondaryContact);

            if(secondaryPreferredEmail != null && secondaryPreferredEmail != '') {
                secondaryContact.rC_Bios__Preferred_Email__c = (secondaryPreferredEmail.toUpperCase().contains('NONE'))? '' : (secondaryPreferredEmail.equalsIgnoreCase('Email') ? 'Home' : 'Work');

                secondaryContact.rC_Bios__Home_Email__c = secondaryEmail;
                secondaryContact.rC_Bios__Work_Email__c = secondaryEmail2;
            }

            if(secondaryPreferredPhone != null && secondaryPreferredPhone != ''){

                if(secondaryPreferredPhone.equalsIgnoreCase('Home Phone')){
                    secondaryContact.rC_Bios__Preferred_Phone__c = 'Home';
                    //secondaryContact.HomePhone = (secondaryHomePhone != null && secondaryHomePhone != '') ? secondaryHomePhone : secondaryHomePhone;
                }

                if(secondaryPreferredPhone.equalsIgnoreCase('Business Phone')){
                    secondaryContact.rC_Bios__Preferred_Phone__c = 'Work';
                    //secondaryContact.rC_Bios__Work_Phone__c = (secondaryBusinessPhone != null && secondaryBusinessPhone  != '') ? secondaryBusinessPhone : secondaryHomePhone;
                }

                if(secondaryPreferredPhone.equalsIgnoreCase('Mobile Phone')){
                    secondaryContact.rC_Bios__Preferred_Phone__c = 'Mobile';
                    //secondaryContact.MobilePhone = (secondaryMobilePhone != null && secondaryMobilePhone != '') ? secondaryMobilePhone : secondaryHomePhone;
                }
            }

            secondaryContact.HomePhone = (secondaryHomePhone != null && secondaryHomePhone != '') ? secondaryHomePhone : '';
            secondaryContact.rC_Bios__Work_Phone__c = (secondaryBusinessPhone != null && secondaryBusinessPhone  != '') ? secondaryBusinessPhone : '';
            secondaryContact.MobilePhone = (secondaryMobilePhone != null && secondaryMobilePhone != '') ? secondaryMobilePhone : '';

            if(secondaryZipCode != null && secondaryZipCode != ''){
                zipCodeList = [
                    Select Id
                         , Name
                         , Council__c
                         , Zip_Code_Unique__c
                         , City__c
                         , Recruiter__c
                         , Recruiter__r.IsActive
                         , Recruiter__r.UserRoleId
                      From Zip_Code__c
                     where Zip_Code_Unique__c like :secondaryZipCode limit 1
                ];
                system.debug('=55==>'+zipCodeList);

                secondaryZipCodeRecord = (zipCodeList != null && zipCodeList.size() > 0) ? zipCodeList[0] : new Zip_Code__c();
                system.debug('=66===>'+secondaryZipCodeRecord);

                if( secondaryZipCodeRecord != null && secondaryZipCodeRecord.Id != null &&
                    secondaryZipCodeRecord.Recruiter__c != null && secondaryZipCodeRecord.Recruiter__r.IsActive &&
                    secondaryZipCodeRecord.Recruiter__r.UserRoleId != null) {
                    secondaryContact = GirlRegistrationUtilty.upsertContactOwner(secondaryContact, secondaryZipCodeRecord.Recruiter__c);
                }
                else
                    secondaryContact = GirlRegistrationUtilty.upsertContactOwner(secondaryContact, systemAdminUser.Id);

                //if(secondaryContact != null)
                //    insert secondaryContact;
            }
        }
        return secondaryContact;
    }

    public void fillPricebookEntryList() {
        PricebookEntryList = [
            Select Id
                 , Name
                 , Pricebook2.Description
                 , Pricebook2.IsActive
                 , Pricebook2.Name
                 , Pricebook2.Id
                 , Pricebook2Id
                 , Product2Id
                 , UnitPrice
                 , Product2.rC_Giving__Start_Date__c
                 , Product2.rC_Giving__End_Date__c
                 , Product2.Name
              From PricebookEntry
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
               and IsActive = true
        ];
        system.debug('PricebookEntryList=====:  ' + PricebookEntryList);

        if(PricebookEntryList != null && PricebookEntryList.size() > 0) {
            for(PricebookEntry pricebookEntry : PricebookEntryList)
                priceBookEntryMap.put(pricebookEntry.Id, pricebookEntry);
        }
    }

     public List<SelectOption> getcustodialCareInfo() {
        List<SelectOption> custodialCareInfo = new List<SelectOption>();
        List<Contact> adultContactList;
        if(girlContact != null && girlContact.AccountId != null)
            adultContactList = new List<Contact>([select Id from Contact
                                                            where rC_Bios__Role__c = 'Adult'
                                                            AND AccountId =: girlContact.AccountId]);
        if(adultContactList!=null && adultContactList.size()>1){
            custodialCareInfo.add(new Selectoption('--None--', '--None--'));
            custodialCareInfo.add(new SelectOption('Parent', 'Parent'));
            custodialCareInfo.add(new SelectOption('Guardian', 'Guardian'));
        }
        else{
            custodialCareInfo.add(new Selectoption('--None--', '--None--'));
            custodialCareInfo.add(new SelectOption('Both Parents', 'Both Parents'));
            custodialCareInfo.add(new SelectOption('Parent and Secondary Contact', 'Parent and Secondary Contact'));
            custodialCareInfo.add(new SelectOption('Parent', 'Parent'));
            custodialCareInfo.add(new SelectOption('Guardian', 'Guardian'));
            custodialCareInfo.add(new SelectOption('Guardian and Secondary Contact', 'Guardian and Secondary Contact'));
        }

        return custodialCareInfo;
    }

    public List<SelectOption> getPreferredEmails() {
        List<SelectOption> emailOptions = new List<SelectOption>();
        emailOptions.add(new Selectoption('--None--', '--None--'));
        emailOptions.add(new SelectOption('Email', 'Email'));
        emailOptions.add(new SelectOption('Email 2', 'Email 2'));
        return emailOptions;
    }

    public List<SelectOption> getGenders() {
        List<SelectOption> genderOptions = new List<SelectOption>();
        genderOptions.add(new SelectOption('Female', 'Female'));
        genderOptions.add(new SelectOption('Male', 'Male'));
        return genderOptions;
    }

    public List<SelectOption> getPreferredPhones() {
        List<SelectOption> phoneOptions = new List<SelectOption>();
        phoneOptions.add(new Selectoption('--None--', '--None--'));
        phoneOptions.add(new SelectOption('Home Phone', 'Home Phone'));
        phoneOptions.add(new SelectOption('Business Phone', 'Business Phone'));
        phoneOptions.add(new SelectOption('Mobile Phone', 'Mobile Phone'));
        return phoneOptions;
    }

    public List<SelectOption> getlistCountryItems() {
        List<SelectOption> countryOptions = new List<SelectOption>();
        map<String, CountryNames__c> countryNamesMap = CountryNames__c.getAll();

        //countryOptions.add(new Selectoption('--None--', '--None--'));
        if(!countryNamesMap.isEmpty()){
            for(String countryName : countryNamesMap.keySet())
                countryOptions.add(new Selectoption(countryName, countryName));
        }
        return countryOptions;
    }

     public List<SelectOption> getlistStateItems() {
        List<StateNames__c> stateNamesList = StateNames__c.getAll().values();
        List<String> stateNamesSortedList = new List<String>();
        List<SelectOption> stateOptions = new List<SelectOption>();

        if(stateNamesList != null && stateNamesList.size() > 0){
            for(StateNames__c stateName : stateNamesList)
                stateNamesSortedList.add(stateName.Name);
        }

        stateNamesSortedList.sort();
        stateOptions.add(new SelectOption('--None--', '--None--'));
        for(String stateName : stateNamesSortedList)
            stateOptions.add(new SelectOption(stateName, stateName));

        return stateOptions;
    }

    public PageReference primaryAddressSave() {
        Savepoint savepoint = Database.setSavepoint();
        system.debug('streetLine2#######'+streetLine2);
        try{
            if(streetLine1 != null && streetLine1 != '')
                primaryStreetLine1 = streetLine1;
            if(streetLine2 != null && streetLine2 != '')
                primaryStreetLine2 = streetLine2;
            if(city != null && city != '')
                primaryCity = city;
            if(country != null && country != '')
                primaryCountry = country;
            if(county != null && county != '')
                primaryCounty = county;
            if(state != null && state != '')
                primaryState = state;
                //primaryState = GirlRegistrationUtilty.getStateName(state);  //primaryState = state;

            if(zipCode != null && zipCode != '')
                primaryZipCode = zipCode;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public PageReference secondaryAddressSave() {
        Savepoint savepoint = Database.setSavepoint();
        system.debug('streetLine2#######'+streetLine2);
        try{
            if(streetLine1 != null && streetLine1 != '')
                secondaryStreetLine1 = streetLine1;
            if(streetLine2 != null && streetLine2 != '')
                secondaryStreetLine2 = streetLine2;
            if(city != null && city != '')
                secondaryCity = city;
            if(country != null && country != '')
                secondaryCountry = country;
            if(county != null && county != '')
                secondaryCounty = county;
            if(state != null && state != '')
                secondaryState = state;
                //secondaryState = GirlRegistrationUtilty.getStateName(state);

            if(zipCode != null && zipCode != '')
                secondaryZipCode = zipCode;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public PageReference submit() {

        counterUnableToLockRow++;
        string membershipYear = '';

        Opportunity newOpportunity;
        OpportunityLineItem opportunityLineItem;
        OpportunityLineItem councilServiceFeeLineItem;
        PricebookEntry priceBookEntry;
        CampaignMember[] campaignMemberList;

        errorMessage = validateRequiredFields();
        if(errorMessage != null && errorMessage != '')
            return addErrorMessage(errorMessage);

        if(primaryPreferredEmail.equalsIgnoreCase('Email 2') && primaryEmail2.equalsIgnoreCase('')) {
                return addErrorMessage('Email 2 cannot be left blank when selecting Email 2 as Preferred.');
        }
        if(secondaryPreferredEmail <> '' && secondaryPreferredEmail <>  null){
            if(secondaryPreferredEmail.equalsIgnoreCase('Email 2') && secondaryEmail2.equalsIgnoreCase(''))
                    return addErrorMessage('Email 2 of secondary contact cannot be left blank when selecting Email 2 as Preferred.');
        }
        if(primaryPreferredPhone <> '' && primaryPreferredPhone.equalsIgnoreCase('Mobile Phone')) {
            if(primaryMobilePhone == '' || primaryMobilePhone == null)
                    return addErrorMessage('Mobile Phone cannot be left blank while selecting Preffered Phone as Mobile Phone.');
        }
        if(primaryPreferredPhone <> '' && primaryPreferredPhone.equalsIgnoreCase('Business Phone')) {
            if(primaryBusinessPhone == '' || primaryBusinessPhone == null)
                    return addErrorMessage('Business Phone cannot be left blank while selecting Preffered Phone as Business Phone.');
        }
        if(secondaryPreferredPhone <> '' && secondaryPreferredPhone <> null && secondaryPreferredPhone.equalsIgnoreCase('Business Phone')) {
            if(secondaryBusinessPhone == '' || secondaryBusinessPhone == null)
                    return addErrorMessage('Secondary Business Phone cannot be left blank while selecting Preffered Phone as Business Phone.');
        }
        if(secondaryPreferredPhone <> '' && secondaryPreferredPhone <> null && secondaryPreferredPhone.equalsIgnoreCase('Mobile Phone')) {
            if(secondaryMobilePhone == '' || secondaryMobilePhone == null)
                    return addErrorMessage('Secondary Mobile Phone cannot be left blank while selecting Preffered Phone as Mobile Phone.');
        }
        if(booleanTermsAndConditions == false)
            return addErrorMessage('To Proceed, You must agree to accept and abide by the Girl Scout Promise and Law.');

        if(girlContact != null && girlContact.Id != null && girlContact.MailingPostalCode != null)
            zipCode = girlContact.MailingPostalCode;

        Savepoint savepoint = Database.setSavepoint();

        try {
            Zip_Code__c[] zipCodeList = new Zip_Code__c[] {};
            String campaignMembers;
            String[] campaignMemberIdList;
            set<Id> opportunityIdSet = new set<Id>();
            Set<String> campaignMemberIdSet = new Set<String>();
            set<Id> ContactAddressIdSet = new set<Id>();
            List<rC_Bios__Contact_Address__c> insertContactAddressList = new List<rC_Bios__Contact_Address__c>();
            List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
            List<Opportunity> opportunitiesToInsert = new List<Opportunity>();

            if(zipCode != null && zipCode != '')
                zipCode = (zipCode.length() > 5) ? zipCode.substring(0, 5) : zipCode;

            zipCodeList = [
                Select Id
                     , Name
                     , Council__c
                     , Council__r.Service_Fee__c
                     , Council__r.Payment_Campaign__c
                     , Zip_Code_Unique__c
                     , City__c
                     , Recruiter__c
                     , Recruiter__r.IsActive
                     , Recruiter__r.UserRoleId
                  From Zip_Code__c
                 where Zip_Code_Unique__c like :zipCode limit 1
            ];

            matchingZipCode = (zipCodeList.size() > 0) ? zipCodeList[0] : new Zip_Code__c();
            system.debug('zipCodeList==='+zipCodeList);

            if(zipCodeList.size() > 0 && zipCodeList[0].Council__c != null)
                councilServiceFee = zipCodeList[0].Council__r.Service_Fee__c;
            system.debug('councilServiceFee ===: ' + councilServiceFee);

            if(Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds'))
                campaignMembers = Apexpages.currentPage().getParameters().get('CampaignMemberIds');

            if(campaignMembers != null && campaignMembers != '') {
                campaignMemberIdList = campaignMembers.trim().split(',');

                for(String campaignMember : campaignMemberIdList)
                    campaignMemberIdSet.add(campaignMember.trim());
                campaignMemberIdSet.remove(null);
            }

            if(membershipProduct != null && !membershipProduct.toUpperCase().contains('NONE') && priceBookEntryMap != null && priceBookEntryMap.containsKey(membershipProduct.trim()))
                priceBookEntry = priceBookEntryMap.get(membershipProduct.trim());

            if(girlContact == null || priceBookEntry == null)
                 return addErrorMessageAndRollback(savepoint,'No girl contact found.');

            // Added to avoid duplicate membership
            if(priceBookEntry != null && priceBookEntry.Product2.rC_Giving__End_Date__c != null)
                membershipYear = string.valueOf(priceBookEntry.Product2.rC_Giving__End_Date__c.year());

            if(membershipYear !=null && membershipYear != '') {
                List<campaignmember> lstCM = [Select Membership__r.Membership_year__c from campaignmember where ContactId =:girlContact.ID];// and Active__c=true];
                if(lstCM.size() > 0) {
                    for(campaignmember cm : lstCM) {
                        if(cm.Membership__r.Membership_year__c == membershipYear)
                           return addErrorMessageAndRollback(savepoint,'This membership is already active, Please select another membership.');
                    }
                }
            }

            zipCode = (girlContact.MailingPostalCode != null) ? girlContact.MailingPostalCode : zipCode;

            Account[] accountList = [
                Select Id
                     , Name
                     , Phone
                     , ParentId
                     , Instructions__c
                  From Account
                 where Id = :girlContact.AccountId
                 limit 1
            ];
            parentAccount = (accountList != null && accountList.size() > 0) ? accountList[0] : null;
            parentContact = updatePrimaryContact(parentContact);
            girlContact = updateGirlContact(girlContact);
            secondaryContact = createSecondaryContact(parentContact.Id);

            system.debug('\n===========================================================\n' +
                         'parentAccount : ' + accountList +
                         'parentContact : ' + parentContact +
                         'girlContact   : ' + girlContact +
                         '\n===========================================================');

            newOpportunity = createMembershipOpportunity(GirlRegistrationUtilty.getOpportunityRecordTypeId(GirlRegistrationUtilty.MEMBERSHIP_RECORDTYPE), girlContact, priceBookEntry);
            if(newOpportunity != null)
                opportunitiesToInsert.add(newOpportunity);
            campaignMemberList = getCampaignMember(girlContact.Id, campaignMemberIdSet);

            /**********************************************************************************************************/
            if(councilServiceFee != null && councilServiceFee > 0) {

                Opportunity councilMembershipOpportunity = new Opportunity(
                    RecordTypeId = GirlRegistrationUtilty.getOpportunityRecordTypeId(GirlRegistrationUtilty.MEMBERSHIP_RECORDTYPE),
                    AccountId = parentAccount.Id,
                    Contact__c = parentContact.Id,
                    Girl_First_Name__c = girlContact.FirstName,
                    CampaignId = matchingZipCode.Council__r.Payment_Campaign__c,
                    rC_Giving__Giving_Amount__c = councilServiceFee,
                    StageName = 'Open',
                    CloseDate = System.today(),
                    rC_Giving__Is_Giving__c = true
                );

                if(matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.IsActive == true && matchingZipCode.Recruiter__r.UserRoleId != null)
                    councilMembershipOpportunity.OwnerId = matchingZipCode.Recruiter__c;
                else if(systemAdminUser != null && systemAdminUser.Id != null)
                    councilMembershipOpportunity.OwnerId = systemAdminUser.Id;

                if(councilMembershipOpportunity != null && councilMembershipOpportunity.CampaignId != null)
                    opportunitiesToInsert.add(councilMembershipOpportunity);
            }

            system.debug('opportunitiesToInsert ===: ' + opportunitiesToInsert);
            if(opportunitiesToInsert != null && opportunitiesToInsert.size() > 0) {
                insert opportunitiesToInsert;

                List<OpportunityLineItem> OpportunityLineItemInsertList = new List<OpportunityLineItem>();
                PricebookEntry appropriatePricebookEntry;
                PricebookEntry councilPricebookEntry = new PricebookEntry();

                for(Opportunity opp : opportunitiesToInsert) {
                    opportunityIdSet.add(opp.Id);
                    if(matchingZipCode != null && matchingZipCode.Council__c != null && opp.CampaignId == matchingZipCode.Council__r.Payment_Campaign__c)
                        councilMembershipOpp = opp;
                }

                if(girlContact != null && councilMembershipOpp != null && councilMembershipOpp.Id != null) {
                    OpportunityContactRole councilOppContactRole = createOpportunityContactRole(girlContact, councilMembershipOpp);
                }

                for(PricebookEntry varPricebookEntry : priceBookEntryMap.values()) {
                    if(varPricebookEntry.Product2.Name.equalsIgnoreCase(COUNCIL_SERVICE_FEE) && varPricebookEntry.Pricebook2.Name.equalsIgnoreCase(GIRL_SCOUTS_USA_PRICEBOOK)) {
                        councilPricebookEntry = priceBookEntryMap.get(varPricebookEntry.Id);
                        break;
                    }
                }
                for(Opportunity membershipOpportunity : opportunitiesToInsert) {
                    appropriatePricebookEntry = new PricebookEntry();
                    appropriatePricebookEntry = (membershipOpportunity.CampaignId == matchingZipCode.Council__r.Payment_Campaign__c)
                                                ? councilPricebookEntry
                                                : priceBookEntry;

                    if(membershipOpportunity != null && membershipOpportunity.Id != null && appropriatePricebookEntry != null && appropriatePricebookEntry.Id != null) {
                        OpportunityLineItem oppLineItem = createOpportunityLineItem(appropriatePricebookEntry, membershipOpportunity);
                        OpportunityLineItemInsertList.add(oppLineItem);
                    }
                }

                if(OpportunityLineItemInsertList != null && OpportunityLineItemInsertList.size() > 0) {
                    insert OpportunityLineItemInsertList;
                }
                List<Opportunity> OpportunityList = [
                    Select RecordType.Name
                         , RecordTypeId
                         , rC_Giving__Parent__c
                      From Opportunity
                     where RecordType.Name = 'Transaction'
                       and rC_Giving__Parent__c IN : opportunityIdSet
                ];

                if(OpportunityList.size() > 0) {
                    for(Opportunity opp : OpportunityList) {
                        if(matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.IsActive == true && matchingZipCode.Recruiter__r.UserRoleId != null)
                            opp.OwnerId = matchingZipCode.Recruiter__c;
                        else if(systemAdminUser != null && systemAdminUser.Id != null)
                            opp.OwnerId = systemAdminUser.Id;
                    }
                    update OpportunityList;
                }
            }
            /**********************************************************************************************************/

            updateOpportunityType(newOpportunity, priceBookEntry, parentContact);
            updateCampaignMembers(campaignMemberList, newOpportunity);

            rC_Bios__Contact_Address__c girlContactAddress = createContactAddress(girlContact, 'Home', streetLine1, streetLine2, city, state, zipCode, country, county);
            insertContactAddressList.add(girlContactAddress);

            rC_Bios__Contact_Address__c parentContactAddress = createContactAddress(parentContact, 'Home', primaryStreetLine1, primaryStreetLine2, primaryCity, primaryState, primaryZipCode, primaryCountry, primaryCounty);
            insertContactAddressList.add(parentContactAddress);

            if(custodialFlag == true && secondaryContact != null) {
                rC_Bios__Contact_Address__c secondaryContactAddress = createContactAddress(secondaryContact, 'Home', secondaryStreetLine1, secondaryStreetLine2, secondaryCity, secondaryState, secondaryZipCode, secondaryCountry, secondaryCounty);
                insertContactAddressList.add(secondaryContactAddress);
            }

            set<String> contactAddressIds = insertContactAddress(insertContactAddressList);

            if(contactAddressIds != null && !contactAddressIds.isEmpty()) {
                contactAddressList = [
                    Select Id
                         , rC_Bios__Contact__r.MailingPostalCode
                         , rC_Bios__Contact__r.rC_Bios__Secondary_Contact__c
                         , rC_Bios__Contact__r.rC_Bios__Role__c
                         , rC_Bios__Contact__r.rC_Bios__Preferred_Contact__c
                         , rC_Bios__Contact__r.Id
                         , rC_Bios__Contact__c
                         , rC_Bios__Address__c
                      From rC_Bios__Contact_Address__c
                     where Id IN :contactAddressIds
                ];
            }

            if(contactAddressList != null && contactAddressList.size() > 0)
                updateAddressOwner(contactAddressList);

            if(booleanOpportunityGrantRequested == true && councilAccount != null) {
                updateCouncilAccountsFinancialAid(councilAccount.Id, booleanOpportunityGrantRequested);
                //COmmneted by SIddhant
                //updateCouncilAccount(councilAccount, booleanOpportunityGrantRequested);
            }
            if(girlContact != null && newOpportunity != null)
                OpportunityContactRole opportunityContactRole = createOpportunityContactRole(girlContact, newOpportunity);

            if (booleanOppMembershipOnPaper || booleanOpportunityGrantRequested) {
                if(parentContact != null && parentContact.Id != null)
                    GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Girl_ThankYou' + '?GirlContactId='+girlContact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+newOpportunity.Id+'&FinancialAidRequired='+String.valueOf(booleanOpportunityGrantRequested)+'&CashOrCheck='+String.valueOf(booleanOppMembershipOnPaper)+'&ParentContactId='+parentContact.Id, parentContact);

                Pagereference landingPage = Page.Girl_ThankYou;
                if(booleanOpportunityGrantRequested != null)
                    landingPage.getParameters().put('FinancialAidRequired',String.valueOf(booleanOpportunityGrantRequested));
                if(booleanOppMembershipOnPaper != null)
                    landingPage.getParameters().put('CashOrCheck',String.valueOf(booleanOppMembershipOnPaper));
                if(girlContact != null && girlContact.Id != null)
                    landingPage.getParameters().put('GirlContactId', girlContact.Id);

                if(parentContact.Id != null)
                    landingPage.getParameters().put('ParentContactId', parentContact.Id);

                if(campaignMembers != null && campaignMembers != '')
                    landingPage.getParameters().put('CampaignMemberIds',campaignMembers);
                if(councilMembershipOpp != null && councilMembershipOpp.Id != null)
                    landingPage.getParameters().put('CouncilMembershipOpp',councilMembershipOpp.Id);
                if(newOpportunity != null) {
                    landingPage.getParameters().put('OpportunityId',newOpportunity.Id);
                }else {
                     return addErrorMessageAndRollback(savepoint,'Membership Opportunity not created.');
                }
                if (councilAccount != null)
                    landingPage.getParameters().put('CouncilId', councilAccount.Id);

                landingPage.setRedirect(true);
                return landingPage;
            }

            if(parentContact != null && parentContact.Id != null && girlContact != null && girlContact.Id != null && councilAccount != null && councilAccount.Id != null && newOpportunity != null && newOpportunity.Id != null)
            {
            
              if(councilMembershipOpp == null){
                GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Girl_PaymentProcessing' + '?GirlContactId='+girlContact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+newOpportunity.Id + '&ParentContactId='+parentContact.Id, parentContact);
                }else{
                GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Girl_PaymentProcessing' + '?GirlContactId='+girlContact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+newOpportunity.Id + '&ParentContactId='+parentContact.Id+'&CouncilMembershipOppId='+councilMembershipOpp.Id, parentContact);
                } 
            
            
            
            
            }

            Pagereference paymentProcessingPage = Page.Girl_PaymentProcessing;
            paymentProcessingPage.getParameters().put('GirlContactId', girlContact.Id);
            paymentProcessingPage.getParameters().put('CampaignMemberIds',campaignMembers);
            paymentProcessingPage.getParameters().put('ParentContactId', parentContact.Id);

            if(councilAccount != null)
                paymentProcessingPage.getParameters().put('CouncilId', councilAccount.Id);
            if(councilMembershipOpp != null && councilMembershipOpp.Id != null)
                paymentProcessingPage.getParameters().put('CouncilMembershipOppId',councilMembershipOpp.Id);

            if(newOpportunity != null && newOpportunity.Id != null)
                paymentProcessingPage.getParameters().put('OpportunityId', newOpportunity.Id);
            else
                 return addErrorMessageAndRollback(savepoint,'Membership Opportunity not created.');

            paymentProcessingPage.setRedirect(true);
            return paymentProcessingPage;
        } catch(System.exception pException) {
            if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                if(counterUnableToLockRow < 3) {
                    Database.rollback(savepoint);
                    return submit();
                }
                else
                    return addErrorMessage('Record is locked by another user. Please re-submit the page once more.');
            }
            else
                return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public void updateAddressOwner(List<rC_Bios__Contact_Address__c> ContactAddressList) {
        set<Id> addressIdSet = new set<Id>();
        set<String> zipCodeSet = new set<String>();
        Set<rC_Bios__Address__c> addressToUpdateSet = new set<rC_Bios__Address__c>();
        List<rC_Bios__Address__c> addressToUpdateList = new List<rC_Bios__Address__c>();
        map<rC_Bios__Address__c, Id> addressToOwnerIdMap = new map<rC_Bios__Address__c, Id>();
        map<String, rC_Bios__Contact_Address__c> mailingPostalCodeToContactAddressMap = new map<String, rC_Bios__Contact_Address__c>();
        map<String, Zip_Code__c> mailingPostalCodeToZipCodeMap = new map<String, Zip_Code__c>();
        map<Id, rC_Bios__Address__c> addressMap;
        map<Id, rC_Bios__Address__c> addressIdVSAddressMap = new map<Id, rC_Bios__Address__c>();
        map<Id, String> contactAddressVSCountyMap = new map<Id, String>();

        system.debug('#######'+ContactAddressList);
        if(ContactAddressList != null && ContactAddressList.size() > 0) {
            for(rC_Bios__Contact_Address__c contactAddress : ContactAddressList) {

                if(contactAddress.rC_Bios__Address__c != null)
                    addressIdSet.add(contactAddress.rC_Bios__Address__c);

                if(contactAddress.rC_Bios__Contact__r.MailingPostalCode != null && contactAddress.rC_Bios__Contact__r.MailingPostalCode != ''){
                    zipCodeSet.add(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5));
                    mailingPostalCodeToContactAddressMap.put(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5), contactAddress);
                }
                //mailingPostalCodeToContactAddressMap.put(contactAddress.rC_Bios__Contact__r.MailingPostalCode, contactAddress);

                if(contactAddress.rC_Bios__Contact__r.rC_Bios__Role__c.equalsIgnoreCase('Girl'))
                    contactAddressVSCountyMap.put(contactAddress.Id, county);

                if(contactAddress.rC_Bios__Contact__r.rC_Bios__Role__c.equalsIgnoreCase('Adult') && contactAddress.rC_Bios__Contact__r.rC_Bios__Preferred_Contact__c)
                    contactAddressVSCountyMap.put(contactAddress.Id, primaryCounty);

                if(contactAddress.rC_Bios__Contact__r.rC_Bios__Role__c.equalsIgnoreCase('Adult') && !contactAddress.rC_Bios__Contact__r.rC_Bios__Secondary_Contact__c && !contactAddress.rC_Bios__Contact__r.rC_Bios__Preferred_Contact__c)
                    contactAddressVSCountyMap.put(contactAddress.Id, secondaryCounty);

                system.debug('contactAddressVSCountyMap###########'+contactAddressVSCountyMap);
            }
        }

        system.debug('mailingPostalCodeToContactAddressMap#######'+mailingPostalCodeToContactAddressMap);

        if(addressIdSet != null)
            addressMap = new map<Id, rC_Bios__Address__c>([
                Select Id
                     , OwnerId
                  From rC_Bios__Address__c
                 where Id IN : addressIdSet
                   FOR UPDATE
            ]);
        system.debug('addressMap#######'+addressMap);

        Zip_Code__c[] zipCodeList = [
            Select Id
                 , Name
                 , Council__c
                 , Zip_Code_Unique__c
                 , City__c
                 , Recruiter__c
                 , Recruiter__r.IsActive
                 , Recruiter__r.UserRoleId
              From Zip_Code__c
             where Zip_Code_Unique__c IN :zipCodeSet
        ];

        system.debug('zipCodeList#######'+zipCodeList);

        if(zipCodeList.size() > 0) {
            for(Zip_Code__c zipCode : zipCodeList)
                mailingPostalCodeToZipCodeMap.put(zipCode.Zip_Code_Unique__c, zipCode);
        }

        system.debug('mailingPostalCodeToZipCodeMap#######'+mailingPostalCodeToZipCodeMap);

        if(ContactAddressList.size() > 0) {
            Integer iCount = 0;
            for(rC_Bios__Contact_Address__c contactAddress : ContactAddressList) {
                system.debug('contactAddress#######'+contactAddress.Id);

                rC_Bios__Address__c addressToUpdate = new rC_Bios__Address__c();
                addressToUpdate = addressMap.get(contactAddress.rC_Bios__Address__c);

                iCount++;
                if(!mailingPostalCodeToContactAddressMap.isEmpty() && mailingPostalCodeToContactAddressMap.containsKey(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5))) {

                    if(mailingPostalCodeToZipCodeMap.containsKey(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5))) {
                        Zip_Code__c matchedZipCode = mailingPostalCodeToZipCodeMap.get(contactAddress.rC_Bios__Contact__r.MailingPostalCode.substring(0, 5));
                        if(matchedZipCode.Recruiter__c != null && matchedZipCode.Recruiter__r.IsActive == true && matchedZipCode.Recruiter__r.UserRoleId != null)
                            addressToUpdate.OwnerId = matchedZipCode.Recruiter__c != null ? matchedZipCode.Recruiter__c : addressToUpdate.OwnerId;
                        else if(systemAdminUser != null && systemAdminUser.Id != null)
                            addressToUpdate.OwnerId = systemAdminUser.Id;
                    }
                }
                //addressToUpdate.rC_Bios__County__c = county;

                if(!contactAddressVSCountyMap.isEmpty() && contactAddressVSCountyMap.containsKey(contactAddress.Id))
                    addressToUpdate.rC_Bios__County__c = contactAddressVSCountyMap.get(contactAddress.Id);

                if(addressToUpdate != null && addressToUpdate.Id != null)
                    addressIdVSAddressMap.put(addressToUpdate.Id, addressToUpdate);
                    //addressToUpdateList.add(addressToUpdate);
                //addressToUpdateSet.add(addressToUpdate);
            }
        }

        addressToUpdateList = addressIdVSAddressMap.values();

        update addressToUpdateList;
    }

    public Opportunity createMembershipOpportunity(String recordTypeId, Contact contact, PricebookEntry priceBookEntry) {

        String campaignName = '';
        String membershipYear = string.valueOf(system.today().year());

        Opportunity opportunity;

        if(priceBookEntry != null && priceBookEntry.Product2.Name.toUpperCase().contains('LIFETIME')) {
            campaignName = LIFETIME_MEMBERSHIP;
        }
        else if(priceBookEntry != null && priceBookEntry.Product2.rC_Giving__End_Date__c != null) {
            membershipYear = string.valueOf(priceBookEntry.Product2.rC_Giving__End_Date__c.year());
            campaignName = membershipYear + ' Membership';
        }

        Campaign campaign  = GirlRegistrationUtilty.searchCampaignFromName(campaignName);

        if(parentAccount != null && parentAccount.Id != null && campaign != null && campaign.Id != null) {
            opportunity = new Opportunity(
                RecordTypeId = recordTypeId,
                AccountId = parentAccount.Id,
                CampaignId = campaign.Id,
                rC_Giving__Giving_Amount__c = (priceBookEntry.UnitPrice != null) ? priceBookEntry.UnitPrice : 0,
                StageName = 'Open',
                CloseDate = System.today(),
                Membership_Year__c = membershipYear,
                rC_Giving__Is_Giving__c = true
            );

            if(contact.FirstName != null)
                opportunity.Girl_First_Name__c = contact.FirstName;

            if(matchingZipCode != null && matchingZipCode.Recruiter__c != null && matchingZipCode.Recruiter__r.IsActive == true && matchingZipCode.Recruiter__r.UserRoleId != null)
                opportunity.OwnerId = matchingZipCode.Recruiter__c;
            else if(systemAdminUser != null && systemAdminUser.Id != null)
                opportunity.OwnerId = systemAdminUser.Id;

            if(booleanOppMembershipOnPaper)
                opportunity.Membership_on_Paper__c = true;
            if(booleanOpportunityGrantRequested)
                opportunity.Grant_Requested__c = true;

            //insert opportunity;
        }
        return opportunity;
    }

    public void updateCampaignMembers(CampaignMember[] campaignMemberList, Opportunity newOpportunity) {
        if(campaignMemberList != null && campaignMemberList.size() > 0 && newOpportunity != null) {
            for(CampaignMember campaignMember : campaignMemberList)
                campaignMember.Membership__c = newOpportunity != null ? newOpportunity.Id : campaignMember.Membership__c;
            update campaignMemberList;
        }
    }

    public CampaignMember[] getCampaignMember(String contactId, Set<String> campaignMemberIdSet) {
        CampaignMember[] campaignMemberList = [
            Select Id
                 , Why_are_you_unsure__c
                 , Membership__c
                 , Membership_Status__c
                 , ContactId
                 , CampaignId
                 , Account__c
              From CampaignMember
             where ContactId = :contactId
               and Id IN :campaignMemberIdSet
               FOR UPDATE
        ];
        return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList : new List<CampaignMember>();
    }

    /*public set<String> insertContactAddress(List<rC_Bios__Contact_Address__c> insertContactAddressList) {
        set<Id> contactIdSet = new set<Id>();
        set<String> contactAddressIds = new set<String>();
        set<String> addressUniqueKeySet = new set<String>();
        set<String> contactAddrUniqueKeySet = new set<String>();
        List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        map<Id, rC_Bios__Address__c> oldAddressMap = new map<Id, rC_Bios__Address__c>();
        map<String, rC_Bios__Address__c> oldUniqueKeyToAddressMap = new map<String, rC_Bios__Address__c>();
        map<String, rC_Bios__Contact_Address__c> uniqueKeyToContactAddressMap = new map<String, rC_Bios__Contact_Address__c>();

        map<String, rC_Bios__Contact_Address__c> uniqueKeyAndContactIdVSContactAddressMap = new map<String, rC_Bios__Contact_Address__c>();

        system.debug('insertContactAddressList#11#######'+insertContactAddressList);

        if(insertContactAddressList != null && insertContactAddressList.size() > 0) {
            rC_Bios__Address__c address;

            for(rC_Bios__Contact_Address__c contactAddress : insertContactAddressList) {

                if(contactAddress != null && contactAddress.rC_Bios__Contact__c != null)
                    contactIdSet.add(contactAddress.rC_Bios__Contact__c);


                address = new rC_Bios__Address__c();
                address.rC_Bios__Street_Line_1__c = (contactAddress.rC_Bios__Original_Street_Line_1__c != null && contactAddress.rC_Bios__Original_Street_Line_1__c != '') ? contactAddress.rC_Bios__Original_Street_Line_1__c : null;
                address.rC_Bios__Street_Line_2__c = (contactAddress.rC_Bios__Original_Street_Line_2__c != null && contactAddress.rC_Bios__Original_Street_Line_2__c != '') ? contactAddress.rC_Bios__Original_Street_Line_2__c : null;
                address.rC_Bios__City__c = (contactAddress.rC_Bios__Original_City__c != null && contactAddress.rC_Bios__Original_City__c != '') ? contactAddress.rC_Bios__Original_City__c : null;
                address.rC_Bios__State__c = contactAddress.rC_Bios__Original_State__c;
                address.rC_Bios__Postal_Code__c = (contactAddress.rC_Bios__Original_Postal_Code__c != null && contactAddress.rC_Bios__Original_Postal_Code__c != '') ? contactAddress.rC_Bios__Original_Postal_Code__c.substring(0, 5) : null;
                address.rC_Bios__Country__c = (contactAddress.rC_Bios__Original_Country__c != null && contactAddress.rC_Bios__Original_Country__c != '') ? contactAddress.rC_Bios__Original_Country__c : null;

                String addrUniqueKey = generateUniqueMD5(address);
                addressUniqueKeySet.add(addrUniqueKey);

                uniqueKeyToContactAddressMap.put(addrUniqueKey, contactAddress);

                uniqueKeyAndContactIdVSContactAddressMap.put(addrUniqueKey + '_' + contactAddress.rC_Bios__Contact__c, contactAddress);


            }
             system.debug('=== oldAddressMap ===: ' + oldAddressMap);
            system.debug('=== uniqueKeyAndContactIdVSContactAddressMap ===: ' + uniqueKeyAndContactIdVSContactAddressMap);

            if(addressUniqueKeySet != null && addressUniqueKeySet.size() > 0) {
                oldAddressMap = new map<Id, rC_Bios__Address__c>([
                    Select Id, Name, rC_Bios__Unique_MD5__c
                      From rC_Bios__Address__c
                     where rC_Bios__Unique_MD5__c IN :addressUniqueKeySet
                ]);
            }

            system.debug('=== oldAddressMap ===: ' + oldAddressMap);

            if(oldAddressMap.size() > 0) {
                for(rC_Bios__Address__c vAddress : oldAddressMap.values()) {
                    //contactAddrUniqueKeySet.add(contactAddress.rC_Bios__Contact__c + '' + vAddress.Id);
                    oldUniqueKeyToAddressMap.put(vAddress.rC_Bios__Unique_MD5__c, vAddress);
                }
            }

            for(rC_Bios__Contact_Address__c contactAddress : insertContactAddressList) {
                address = new rC_Bios__Address__c();

                address.rC_Bios__Street_Line_1__c = (contactAddress.rC_Bios__Original_Street_Line_1__c != null && contactAddress.rC_Bios__Original_Street_Line_1__c != '') ? contactAddress.rC_Bios__Original_Street_Line_1__c : null;
                address.rC_Bios__Street_Line_2__c = (contactAddress.rC_Bios__Original_Street_Line_2__c != null && contactAddress.rC_Bios__Original_Street_Line_2__c != '') ? contactAddress.rC_Bios__Original_Street_Line_2__c : null;
                address.rC_Bios__City__c = (contactAddress.rC_Bios__Original_City__c != null && contactAddress.rC_Bios__Original_City__c != '') ? contactAddress.rC_Bios__Original_City__c : null;
                address.rC_Bios__State__c = contactAddress.rC_Bios__Original_State__c;
                address.rC_Bios__Postal_Code__c = (contactAddress.rC_Bios__Original_Postal_Code__c != null && contactAddress.rC_Bios__Original_Postal_Code__c != '') ? contactAddress.rC_Bios__Original_Postal_Code__c.substring(0, 5) : null;
                address.rC_Bios__Country__c = (contactAddress.rC_Bios__Original_Country__c != null && contactAddress.rC_Bios__Original_Country__c != '') ? contactAddress.rC_Bios__Original_Country__c : null;

                String addrUniqueKey = generateUniqueMD5(address);
                if(oldUniqueKeyToAddressMap != null && oldUniqueKeyToAddressMap.containsKey(addrUniqueKey)) {
                    contactAddrUniqueKeySet.add(contactAddress.rC_Bios__Contact__c + '' + oldUniqueKeyToAddressMap.get(addrUniqueKey).Id);
                }
            }
        }

        Map<String,rC_Bios__Contact_Address__c> contactAdressMapToUpsert = new Map<String,rC_Bios__Contact_Address__c>();

        Map<String, List<rC_Bios__Contact_Address__c>> contactAdressMapToUpsert1 = new Map<String, List<rC_Bios__Contact_Address__c>>();


        if(!contactIdSet.isEmpty()) {
            contactAddressList = [
                Select Id
                     , rC_Bios__Preferred_Mailing__c
                     , rC_Bios__Contact__c
                     , rC_Bios__Address__r.rC_Bios__Unique_MD5__c
                  From rC_Bios__Contact_Address__c
                 Where rC_Bios__Contact__c IN :contactIdSet
                   //and rC_Bios__Preferred_Mailing__c = true
                   //and Contact_Address_UniqueKey__c  NOT IN :contactAddrUniqueKeySet
            ];

            system.debug('contactAddressList===>'+contactAddressList);

            if(contactAddressList != null && contactAddressList.size() > 0) {
                for(rC_Bios__Contact_Address__c contactAddressNew : contactAddressList) {

                        system.debug('contactAddressNew######'+contactAddressNew);
                        system.debug('uniqueKeyAndContactIdVSContactAddressMap######'+uniqueKeyAndContactIdVSContactAddressMap);

                        system.debug('contactAddressNew.rC_Bios__Address__r.rC_Bios__Unique_MD5__ccontactAddressNew.rC_Bios__Contact__c######'+contactAddressNew.rC_Bios__Address__r.rC_Bios__Unique_MD5__c+'_'+contactAddressNew.rC_Bios__Contact__c);

                        //rC_Bios__Contact_Address__c contactAddressToUpsert = uniqueKeyToContactAddressMap.get(contactAddressNew.rC_Bios__Address__r.rC_Bios__Unique_MD5__c);
                        if(contactAddressNew != null && !uniqueKeyAndContactIdVSContactAddressMap.isEmpty() && uniqueKeyAndContactIdVSContactAddressMap.containsKey(contactAddressNew.rC_Bios__Address__r.rC_Bios__Unique_MD5__c+'_'+contactAddressNew.rC_Bios__Contact__c)) {
                        rC_Bios__Contact_Address__c contactAddressToUpsert = uniqueKeyAndContactIdVSContactAddressMap.get(contactAddressNew.rC_Bios__Address__r.rC_Bios__Unique_MD5__c+'_'+contactAddressNew.rC_Bios__Contact__c);

                        system.debug('contactAddressToUpsert######'+contactAddressToUpsert);
                        system.debug('contactAddressToUpsert.rC_Bios__Address__r.rC_Bios__Unique_MD5__c######'+contactAddressToUpsert.rC_Bios__Address__r.rC_Bios__Unique_MD5__c);

                        system.debug('oldUniqueKeyToAddressMap######'+oldUniqueKeyToAddressMap);

                        if(contactAddressToUpsert != null && contactAddressToUpsert.rC_Bios__Address__r.rC_Bios__Unique_MD5__c != null && !oldUniqueKeyToAddressMap.isEmpty() &&  !oldUniqueKeyToAddressMap.containsKey(contactAddressToUpsert.rC_Bios__Address__r.rC_Bios__Unique_MD5__c)) {
                            if(  contactAddressToUpsert !=null && contactAddressToUpsert.rC_Bios__Contact__c==contactAddressNew.rC_Bios__Contact__c) {

                                //nothing to do as already preferred contact exist
                                if(contactAddressNew.rC_Bios__Preferred_Mailing__c==true){
                                    contactAddressIds.add(contactAddressNew.Id);
                                }
                                else if(contactAddressNew.rC_Bios__Preferred_Mailing__c==false){
                                    //need to update to true, as this is supposed to be inserted
                                    contactAddressNew.rC_Bios__Preferred_Mailing__c = true;
                                    contactAddressToUpsert.Id = contactAddressNew.Id;

                                    List<rC_Bios__Contact_Address__c> rcBiosContactAddressList1 = new List<rC_Bios__Contact_Address__c>();
                                    rcBiosContactAddressList1.add(contactAddressToUpsert);

                                    contactAdressMapToUpsert1.put(contactAddressNew.rC_Bios__Address__r.rC_Bios__Unique_MD5__c, rcBiosContactAddressList1);
                                    //contactAdressMapToUpsert.put(contactAddressNew.rC_Bios__Address__r.rC_Bios__Unique_MD5__c,contactAddressToUpsert);
                                }
                                continue;
                            }
                            else{
                                //no new record found , update existing
                                contactAddressNew.rC_Bios__Preferred_Mailing__c = false;
                            }

                            updateContactAddressList.add(contactAddressNew);
                        }
                }
            }
                if(updateContactAddressList != null && updateContactAddressList.size() > 0)
                    update updateContactAddressList;
            }
        }

        if(insertContactAddressList!=null)
            for(rC_Bios__Contact_Address__c contactAddress : insertContactAddressList){
                rC_Bios__Address__c address = new rC_Bios__Address__c();

                address.rC_Bios__Street_Line_1__c = (contactAddress.rC_Bios__Original_Street_Line_1__c != null && contactAddress.rC_Bios__Original_Street_Line_1__c != '') ? contactAddress.rC_Bios__Original_Street_Line_1__c : null;
                address.rC_Bios__Street_Line_2__c = (contactAddress.rC_Bios__Original_Street_Line_2__c != null && contactAddress.rC_Bios__Original_Street_Line_2__c != '') ? contactAddress.rC_Bios__Original_Street_Line_2__c : null;
                address.rC_Bios__City__c = (contactAddress.rC_Bios__Original_City__c != null && contactAddress.rC_Bios__Original_City__c != '') ? contactAddress.rC_Bios__Original_City__c : null;
                address.rC_Bios__State__c = contactAddress.rC_Bios__Original_State__c;
                address.rC_Bios__Postal_Code__c = (contactAddress.rC_Bios__Original_Postal_Code__c != null && contactAddress.rC_Bios__Original_Postal_Code__c != '') ? contactAddress.rC_Bios__Original_Postal_Code__c.substring(0, 5) : null;
                address.rC_Bios__Country__c = (contactAddress.rC_Bios__Original_Country__c != null && contactAddress.rC_Bios__Original_Country__c != '') ? contactAddress.rC_Bios__Original_Country__c : null;

                system.debug('########address#########'+address);

                String addrUniqueKey = generateUniqueMD5(address);
                system.debug('########addrUniqueKey11#########'+addrUniqueKey);

                //if(!contactAdressMapToUpsert.keySet().contains(addrUniqueKey)){
                //    contactAdressMapToUpsert.put(addrUniqueKey,contactAddress);
                    //system.debug('***** '+contactAddress);
                //}

                if(!oldUniqueKeyToAddressMap.containsKey(addrUniqueKey)) {
                    if(!contactAdressMapToUpsert1.isEmpty() && contactAdressMapToUpsert1.containsKey(addrUniqueKey)) {
                        List<rC_Bios__Contact_Address__c> rCBiosContactAddressList2 = new List<rC_Bios__Contact_Address__c>();
                        rCBiosContactAddressList2 = contactAdressMapToUpsert1.get(addrUniqueKey);
                        rCBiosContactAddressList2.add(contactAddress);
                        system.debug('########11111#########'+rCBiosContactAddressList2);

                        contactAdressMapToUpsert1.put(addrUniqueKey, rCBiosContactAddressList2);
                    }
                    else {
                        List<rC_Bios__Contact_Address__c> rCBiosContactAddressList2 = new List<rC_Bios__Contact_Address__c>();
                        rCBiosContactAddressList2.add(contactAddress);
                        system.debug('########2222#########'+rCBiosContactAddressList2);

                        contactAdressMapToUpsert1.put(addrUniqueKey, rCBiosContactAddressList2);
                    }
                }

            }


        if(contactAdressMapToUpsert1 != null && contactAdressMapToUpsert1.values().size() > 0) {
            system.debug('########33333#########'+contactAdressMapToUpsert1);

            List<Database.UpsertResult> lstContactAddressUpsertResult;
            try {
                system.debug('**** before database.upsert');

                List<rC_Bios__Contact_Address__c> rC_BiosContactAddress55 = new List<rC_Bios__Contact_Address__c>();

                List<List<rC_Bios__Contact_Address__c>> upsertrC_BiosContact_AddressList = new List<List<rC_Bios__Contact_Address__c>>();
                upsertrC_BiosContact_AddressList.addAll(contactAdressMapToUpsert1.values());

                for(List<rC_Bios__Contact_Address__c>  objList :upsertrC_BiosContact_AddressList) {
                    for(rC_Bios__Contact_Address__c BiosContact_Address1 :objList ) {
                        rC_BiosContactAddress55.add(BiosContact_Address1);
                    }
                }

                if(!rC_BiosContactAddress55.isEmpty())
                    lstContactAddressUpsertResult = database.upsert(rC_BiosContactAddress55 , false);
                //database.upsert(pLIST<SObject>, pBoolean)

                system.debug('**** after database.upsert');
            } catch (Exception Ex) {
                system.debug('==== Contact Address Exception ===: ' + Ex.getMessage());
            }

            if(lstContactAddressUpsertResult != null && lstContactAddressUpsertResult.size() > 0) {
                for(Database.UpsertResult upsertResult : lstContactAddressUpsertResult) {
                    if(upsertResult.isSuccess())
                        contactAddressIds.add(upsertResult.getId());
                }
            }
        }
        return contactAddressIds;
    }*/

    public rC_Bios__Contact_Address__c createContactAddress(Contact contact, String addrType, String addrStreetLine1, String addrStreetLine2, String addrCity,
                    String addrState, String addrZipCode, String addrCountry, String addrCounty) {
        rC_Bios__Contact_Address__c contactAddress;
        //List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        //List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();
        system.debug('***** contact'+contact);
        if(contact != null) {
            contactAddress = new rC_Bios__Contact_Address__c();
            contactAddress.rC_Bios__Contact__c = contact.Id;
            contactAddress.rC_Bios__Type__c = (addrType != null) ? addrType : 'Home';
            contactAddress.rC_Bios__Original_Street_Line_1__c = (addrStreetLine1 != null && addrStreetLine1.length() > 255)
                                                                ? addrStreetLine1.substring(0, 255)
                                                                : (addrStreetLine1 == null ? '' : addrStreetLine1);

            contactAddress.rC_Bios__Original_Street_Line_2__c = (addrStreetLine2 != null && addrStreetLine2.length() > 255)
                                                                ? addrStreetLine2.substring(0, 255)
                                                                : (addrStreetLine2 == null ? '' : addrStreetLine2);

            contactAddress.rC_Bios__Original_City__c = addrCity != null ? addrCity : '';

            if(addrState != null){
                //String stateAbbreviation = GirlRegistrationUtilty.getStateAbbreviation(addrState);
                //contactAddress.rC_Bios__Original_State__c = stateAbbreviation != null ? stateAbbreviation : '';
                system.debug('=== addrState ==: ' + addrState + ' == ' + addrState.length());
                contactAddress.rC_Bios__Original_State__c = addrState;
            }
            contactAddress.rC_Bios__Original_Postal_Code__c = addrZipCode != null ? addrZipCode : '';
            contactAddress.rC_Bios__Original_Country__c = addrCountry != null ? addrCountry : '';
            contactAddress.rC_Bios__Preferred_Mailing__c = true;
        }
        return contactAddress;
    }

    public static String generateUniqueMD5(rC_Bios__Address__c address) {
        String base = ':' + address.rC_Bios__Street_Line_1__c
                    + ':' + address.rC_Bios__Street_Line_2__c
                    + ':' + address.rC_Bios__City__c
                    + ':' + address.rC_Bios__State__c
                    + ':' + address.rC_Bios__Postal_Code__c
                    + ':' + address.rC_Bios__Country__c;
        return EncodingUtil.convertToHex(Crypto.generateDigest('MD5', Blob.valueOf(base.toLowerCase())));
    }

    public Zip_Code__c getZipCode(String zipCode) {
        Zip_Code__c[] zipCodeList = [
            Select Id
                 , Name
                 , Council__c
                 , Zip_Code_Unique__c
                 , City__c
                 , Recruiter__c
                 , Recruiter__r.IsActive
                 , Recruiter__r.UserRoleId
              From Zip_Code__c
             where Zip_Code_Unique__c like :zipCode limit 1
        ];

        return (zipCodeList.size() > 0) ?  zipCodeList[0] : new Zip_Code__c();
    }

    public OpportunityLineItem createOpportunityLineItem(PricebookEntry priceBookEntry, Opportunity newOpportunity) {
        OpportunityLineItem opportunityLineItem = new OpportunityLineItem();
        opportunityLineItem.PricebookEntryId = priceBookEntry.Id;
        opportunityLineItem.OpportunityId = newOpportunity.Id;
        opportunityLineItem.Quantity = 1;
        opportunityLineItem.UnitPrice = newOpportunity.rC_Giving__Giving_Amount__c;

        //insert opportunityLineItem;
        return opportunityLineItem;
    }

    public void updateOpportunityType(Opportunity newOpportunity, PricebookEntry priceBookEntry, Contact contact) {

        if(newOpportunity != null && priceBookEntry != null && contact != null) {
            newOpportunity.Type = (priceBookEntry.Name.toUpperCase().contains('GIRL')) ? 'Girl Membership'
                                    :((priceBookEntry.Name.toUpperCase().contains('GIRL')) ? 'Girl Membership' : newOpportunity.Type);

            newOpportunity.Contact__c = contact.Id;
            newOpportunity.Membership_Status__c = (contact.Ineligible__c == true) ? 'Ineligible' : 'Payment Pending';
            newOpportunity.rC_Giving__Primary_Contact__c = contact.Id;
            newOpportunity.Adult_Email__c = (primaryPreferredEmail != null && primaryPreferredEmail != ''
                                            && primaryPreferredEmail.equalsIgnoreCase('Email 2') && primaryEmail2 <> '')
                                            ? primaryEmail2 : '' ;
            update newOpportunity;
        }
    }

    public OpportunityContactRole createOpportunityContactRole(Contact contact, Opportunity newOpportunity) {
        OpportunityContactRole opportunityContactRole = new OpportunityContactRole(
            Role = 'Girl',
            OpportunityId = newOpportunity.Id,
            ContactId =  contact.Id,
            IsPrimary = true
        );
        insert opportunityContactRole;
        return opportunityContactRole;

        return null;
    }

    public void updateCouncilAccount(Account councilAccount, Boolean booleanOppGrantRequested) {
        councilAccount.Volunteer_Financial_Aid_Available__c = booleanOppGrantRequested;
        update councilAccount;
    }

    private string validateRequiredFields() {
        errorMessage = '';
        errorMessage = (membershipProduct == null || membershipProduct.contains('None')) ? errorMessage + 'Please select a Membership <br/>' : errorMessage;
        errorMessage = (dateOfBirth == null || dateOfBirth == '') ? errorMessage + 'Please enter a birth date <br/>' : errorMessage;

        errorMessage = (streetLine1 == null || streetLine1 == '') ? errorMessage + 'Please enter a Girl Street Line 1 <br/>' : errorMessage;
        errorMessage = (city == null || city == '') ? errorMessage + 'Please enter a Girl City <br/>' : errorMessage;
        errorMessage = (state == null || state == '') ? errorMessage + 'Please enter a Girl State <br/>' : errorMessage;
        errorMessage = (zipCode == null || zipCode == '') ? errorMessage + 'Please enter a Girl zip code <br/>' : errorMessage;
        errorMessage = (county == null || county == '') ? errorMessage + 'Please enter a Girl County <br/>' : errorMessage;
        errorMessage = (country == null || country == '') ? errorMessage + 'Please enter a Girl Country <br/>' : errorMessage;

        errorMessage = (primaryFirstName == null || primaryFirstName == '') ? errorMessage + 'Please enter a First Name for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryLastName == null || primaryLastName == '') ? errorMessage + 'Please enter a Last Name for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryEmail == null || primaryEmail == '') ? errorMessage + 'Please enter an Email for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryPreferredEmail == null || primaryPreferredEmail == '') ? errorMessage + 'Please select a preferred email for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryHomePhone == null || primaryHomePhone == '') ? errorMessage + 'Please enter a home phone for a primary Contact <br/>' : errorMessage;
        errorMessage = (primaryPreferredPhone == null || primaryPreferredPhone == '') ? errorMessage + 'Please select a preferred phone for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryStreetLine1 == null || primaryStreetLine1 == '') ? errorMessage + 'Please enter a street line 1 for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryCity == null || primaryCity == '') ? errorMessage + 'Please enter a city for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryState == null || primaryState == '') ? errorMessage + 'Please enter a state for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryZipCode == null || primaryZipCode == '') ? errorMessage + 'Please enter a zip code for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryCounty == null || primaryCounty == '') ? errorMessage + 'Please enter a county for a primary contact <br/>' : errorMessage;
        errorMessage = (primaryCountry == null || primaryCountry == '') ? errorMessage + 'Please enter a country for a primary contact <br/>' : errorMessage;

        if(custodialFlag == true) {
            errorMessage = (secondaryFirstName == null || secondaryFirstName == '') ? errorMessage + 'Please enter a first name for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryLastName == null || secondaryLastName == '') ? errorMessage + 'Please enter a last name for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryEmail == null || secondaryEmail == '') ? errorMessage + 'Please enter an email for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryPreferredEmail == null || secondaryPreferredEmail == '') ? errorMessage + 'Please select a preferred email for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryHomePhone == null || secondaryHomePhone == '') ? errorMessage + 'Please enter a home phone for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryPreferredPhone == null || secondaryPreferredPhone == '') ? errorMessage + 'Please select a preferred phone for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryStreetLine1 == null || secondaryStreetLine1 == '') ? errorMessage + 'Please enter a streetline 1 for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryCity == null || secondaryCity == '') ? errorMessage + 'Please enter a city for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryState == null || secondaryState == '') ? errorMessage + 'Please enter a state for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryZipCode == null || secondaryZipCode == '') ? errorMessage + 'Please enter a zip code for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryCounty == null || secondaryCounty == '') ? errorMessage + 'Please enter a county for a secondary contact <br/>' : errorMessage;
            errorMessage = (secondaryCountry == null || secondaryCountry == '') ? errorMessage + 'Please enter a country for a secondary contact <br/>' : errorMessage;
        }
        system.debug('== errorMessage ==:  ' + errorMessage);
        return errorMessage;
    }


    @future
    public static void updateGirlContactInfo (Id contactToUpdateId, String dateOfBirth, String selectedCustodialInfo, String girlEmail, String primaryEmail, String girlPhone, Boolean emailOptIn, Boolean textOptIn, string strGirlPhone,boolean booleanContactPhotoOptIn) {

        system.debug('***contactToUpdateId***'+contactToUpdateId);
        if(contactToUpdateId != null) {

            Contact contactToUpdate= [SELECT Id, rC_Bios__Birth_Day__c, rC_Bios__Birth_Month__c, rC_Bios__Birth_Year__c, Custodial_Care__c, rC_Bios__Home_Email__c, HomePhone, rC_Bios__Preferred_Email__c, rC_Bios__Preferred_Phone__c, Email_Opt_In__c, Text_Phone_Opt_In__c FROM Contact WHERE Id =:contactToUpdateId];

            String[] strBirthDate;

            if(dateOfBirth.contains('/')) {
                strBirthDate = dateOfBirth.trim().split('/');
            }
            system.debug('***selectedCustodialInfo***'+selectedCustodialInfo);

            contactToUpdate.rC_Bios__Birth_Day__c=strBirthDate[1].trim();
            contactToUpdate.rC_Bios__Birth_Month__c=strBirthDate[0].trim();
            contactToUpdate.rC_Bios__Birth_Year__c=strBirthDate[2].trim();
            if(selectedCustodialInfo <> null && selectedCustodialInfo <> '') {
                contactToUpdate.Custodial_Care__c = selectedCustodialInfo;
            }
            contactToUpdate.rC_Bios__Home_Email__c = (girlEmail != null && girlEmail != '') ? girlEmail : primaryEmail;
            contactToUpdate.HomePhone = (girlPhone != '' && girlPhone != null ) ? girlPhone : strGirlPhone;
            contactToUpdate.rC_Bios__Preferred_Email__c = 'Home';
            contactToUpdate.rC_Bios__Preferred_Phone__c = 'Home';
            contactToUpdate.Email_Opt_In__c = emailOptIn;
            contactToUpdate.Text_Phone_Opt_In__c = textOptIn;
            contactToUpdate.Photo_Opt_In__c = booleanContactPhotoOptIn;

            update contactToUpdate;
        }
    }


/*    @future
            String primaryHomePhone, String primaryBusinessPhone, String primaryMobilePhone, Boolean primaryEmailOptIn, Boolean primaryTextOptIn, Boolean booleanContactPhotoOptIn) {
            Contact contactToUpdate= [SELECT Id
                                           , Custodial_Care__c
                                           , FirstName
                                           , LastName
                                           , rC_Bios__Gender__c
                                           , rC_Bios__Preferred_Email__c
                                           , Volunteer_Terms_and_Conditions__c
                                           , rC_Bios__Home_Email__c
                                           , rC_Bios__Work_Email__c
                                           , rC_Bios__Preferred_Phone__c
                                           , HomePhone
                                           , rC_Bios__Work_Phone__c
                                           , MobilePhone
                                           , Photo_Opt_In__c
                                           , Email_Opt_In__c
                                           , Text_Phone_Opt_In__c
                                        FROM Contact
                                       WHERE Id =:contactToUpdateId
            ];

            contactToUpdate.FirstName = (primaryFirstName != null && primaryFirstName != '' && primaryFirstName.length() > 40) ? primaryFirstName.substring(0, 40) : primaryFirstName;
            contactToUpdate.LastName  = (primaryLastName  != null && primaryLastName != '' && primaryLastName.length()  > 80) ? primaryLastName.substring(0, 80) : primaryLastName;

            contactToUpdate.rC_Bios__Gender__c = (primaryGender != null && primaryGender != '' && primaryGender.toUpperCase().contains('NONE')) ? '' : primaryGender;
            contactToUpdate.rC_Bios__Preferred_Email__c = (primaryPreferredEmail != null && primaryPreferredEmail != '' && primaryPreferredEmail.toUpperCase().contains('NONE'))
                                                              ? ''
                                                              : (primaryPreferredEmail.equalsIgnoreCase('Email') ? 'Home' : 'Work');
            contactToUpdate.Volunteer_Terms_and_Conditions__c = true;
            if(primaryPreferredEmail != null && primaryPreferredEmail != '') {
                if(primaryPreferredEmail.equalsIgnoreCase('Email'))
                    contactToUpdate.rC_Bios__Home_Email__c = primaryEmail;
                if(primaryPreferredEmail.equalsIgnoreCase('Email 2'))
                    contactToUpdate.rC_Bios__Work_Email__c = (primaryEmail2 != null && primaryEmail2 != '') ? primaryEmail2 : '';
            }

            if(primaryPreferredPhone != null && primaryPreferredPhone != ''){

                if(primaryPreferredPhone.equalsIgnoreCase('Home Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Home';
                    contactToUpdate.HomePhone = primaryHomePhone;
                }

                if(primaryPreferredPhone.equalsIgnoreCase('Business Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Work';
                    contactToUpdate.rC_Bios__Work_Phone__c = (primaryBusinessPhone != null && primaryBusinessPhone  != '') ? primaryBusinessPhone : primaryHomePhone;
                }

                if(primaryPreferredPhone.equalsIgnoreCase('Mobile Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Mobile';
                    contactToUpdate.MobilePhone = (primaryMobilePhone != null && primaryMobilePhone != '') ? primaryMobilePhone : primaryHomePhone;
                }
            }

            contactToUpdate.Email_Opt_In__c = primaryEmailOptIn;
            contactToUpdate.Text_Phone_Opt_In__c = primaryTextOptIn;
            contactToUpdate.Photo_Opt_In__c = booleanContactPhotoOptIn;

            //GSA-1110  :  Make primary contact as a preferred contact on household and make existing preferred to false
            List<Contact> contactsToUpdateList = new List<Contact>();
            //system.debug('=== parentAccount ===:  ' + parentAccount);
            if(parentAccount != null && parentAccount.Id != null) {
                List<Contact> preferredContactList = [
                    Select Id
                         , AccountId
                         , rC_Bios__Preferred_Contact__c
                         , Adult_Member__c
                      From Contact
                     where AccountId = :parentAccount.Id
                       and rC_Bios__Preferred_Contact__c = true
                       and Id != :contactToUpdate.Id
                ];

                if(preferredContactList != null && preferredContactList.size() > 0) {
                    for(Contact conToUpdate : preferredContactList) {
                        conToUpdate.rC_Bios__Preferred_Contact__c = false;
                        contactsToUpdateList.add(conToUpdate);
                    }
                    /*
                    update contactsToUpdateList;
                    Needs to resolve : Error:There are multiple contacts on this account marked as preferred
                */
/*                system.debug('== contactsToUpdateList ===:  ' + contactsToUpdateList);
            }
        }

        /*
            contactToUpdate.rC_Bios__Preferred_Contact__c = true;
            Needs to resolve : Error:There are multiple contacts on this account marked as preferred
        */

/*        system.debug('=== contactToUpdate : ' + contactToUpdate);
        update contactToUpdate;
        return contactToUpdate;


    }
*/

    @future
    public static void updateCouncilAccountsFinancialAid (Id councilAccountId, boolean booleanGrantRequested) {
        if(councilAccountId != null && booleanGrantRequested != null) {
            List<Account> accountList = [Select Volunteer_Financial_Aid_Available__c, Id From Account where Id = :councilAccountId];
            Account councilAccount1 = (accountList != null && accountList.size() > 0) ? accountList[0] : new Account();

            if(councilAccount1 != null && councilAccount1.Id != null) {
                councilAccount1.Volunteer_Financial_Aid_Available__c = booleanGrantRequested;
                update councilAccount1;
            }
        }
    }



    public set<String> insertContactAddress(List<rC_Bios__Contact_Address__c> insertContactAddressList) {
        set<Id> contactIdSet = new set<Id>();
        set<String> contactAddressIds = new set<String>();
        set<String> newAddressUniqueKeySet = new set<String>();
        set<String> contactAddrUniqueKeySet = new set<String>();
        List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        map<Id, rC_Bios__Address__c> oldAddressOnContactMap = new map<Id, rC_Bios__Address__c>();
        map<String, rC_Bios__Address__c> oldUniqueKeyToAddressMap = new map<String, rC_Bios__Address__c>();
        map<String, rC_Bios__Contact_Address__c> uniqueKeyToContactAddressMap = new map<String, rC_Bios__Contact_Address__c>();
        map<String, rC_Bios__Contact_Address__c> uniqueAddressAndContactKeyVSContactAddressMap = new map<String, rC_Bios__Contact_Address__c>();

        Map<Id, String> contactIdVSUniqueAddressAndContactKeyMap = new Map<Id, String>();
        Map<String, Id> uniqueAddressAndContactKeyVSContactIdMap = new Map<String, Id>();


        Map<Id, List<rC_Bios__Contact_Address__c>> contactIdVSContactAddrressListMap = new Map<Id, List<rC_Bios__Contact_Address__c>>();
        Map<String, rC_Bios__Contact_Address__c> oldUniqueAddressAndContactKeyVSContactAddressMap = new Map<String, rC_Bios__Contact_Address__c>();


        if(insertContactAddressList != null && insertContactAddressList.size() > 0) {

            for(rC_Bios__Contact_Address__c contactAddress : insertContactAddressList) {

                if(contactAddress != null && contactAddress.rC_Bios__Contact__c != null)
                    contactIdSet.add(contactAddress.rC_Bios__Contact__c);

                rC_Bios__Address__c address = new rC_Bios__Address__c();
                address.rC_Bios__Street_Line_1__c = (contactAddress.rC_Bios__Original_Street_Line_1__c != null && contactAddress.rC_Bios__Original_Street_Line_1__c != '') ? contactAddress.rC_Bios__Original_Street_Line_1__c : null;
                address.rC_Bios__Street_Line_2__c = (contactAddress.rC_Bios__Original_Street_Line_2__c != null && contactAddress.rC_Bios__Original_Street_Line_2__c != '') ? contactAddress.rC_Bios__Original_Street_Line_2__c : null;
                address.rC_Bios__City__c = (contactAddress.rC_Bios__Original_City__c != null && contactAddress.rC_Bios__Original_City__c != '') ? contactAddress.rC_Bios__Original_City__c : null;
                address.rC_Bios__State__c = contactAddress.rC_Bios__Original_State__c;
                address.rC_Bios__Postal_Code__c = (contactAddress.rC_Bios__Original_Postal_Code__c != null && contactAddress.rC_Bios__Original_Postal_Code__c != '') ? contactAddress.rC_Bios__Original_Postal_Code__c.substring(0, 5) : null;
                address.rC_Bios__Country__c = (contactAddress.rC_Bios__Original_Country__c != null && contactAddress.rC_Bios__Original_Country__c != '') ? contactAddress.rC_Bios__Original_Country__c : null;

                String addrUniqueKey = generateUniqueMD5(address);
                newAddressUniqueKeySet.add(addrUniqueKey);
                uniqueAddressAndContactKeyVSContactAddressMap.put(addrUniqueKey + '_' + contactAddress.rC_Bios__Contact__c, contactAddress);
                contactIdVSUniqueAddressAndContactKeyMap.put(contactAddress.rC_Bios__Contact__c, addrUniqueKey + '_' + contactAddress.rC_Bios__Contact__c);
                uniqueAddressAndContactKeyVSContactIdMap.put(addrUniqueKey + '_' + contactAddress.rC_Bios__Contact__c, contactAddress.rC_Bios__Contact__c);

            }
            system.debug('=== newAddressUniqueKeySet#### ' + newAddressUniqueKeySet);
            system.debug('=== uniqueAddressAndContactKeyVSContactAddressMap ===: ' + uniqueAddressAndContactKeyVSContactAddressMap);
            system.debug('=== contactIdVSUniqueAddressAndContactKeyMap#### ' + contactIdVSUniqueAddressAndContactKeyMap);
            system.debug('=== uniqueAddressAndContactKeyVSContactIdMap#### ' + uniqueAddressAndContactKeyVSContactIdMap);

            List<rC_Bios__Contact_Address__c> allContactAddressList = [
                Select rC_Bios__Preferred_Mailing__c
                     , rC_Bios__Contact__r.Id
                     , rC_Bios__Address__r.rC_Bios__Unique_MD5__c
                     , rC_Bios__Address__c
                     , Id
                     //, Contact_Address_UniqueKey__c
                  From rC_Bios__Contact_Address__c
                 where rC_Bios__Contact__c IN :contactIdSet
            ];

            if(!allContactAddressList.isEmpty()) {
                for(rC_Bios__Contact_Address__c contactAddress :allContactAddressList) {
                    if(!contactIdVSContactAddrressListMap.isEmpty() && contactIdVSContactAddrressListMap.containsKey(contactAddress.rC_Bios__Contact__c)) {
                        List<rC_Bios__Contact_Address__c> contactRelatedContactAddressList = contactIdVSContactAddrressListMap.get(contactAddress.rC_Bios__Contact__c);
                        contactRelatedContactAddressList.add(contactAddress);
                        contactIdVSContactAddrressListMap.put(contactAddress.rC_Bios__Contact__r.Id, contactRelatedContactAddressList);
                    }
                    else {
                        List<rC_Bios__Contact_Address__c> contactRelatedContactAddressList = new List<rC_Bios__Contact_Address__c>();
                        contactRelatedContactAddressList.add(contactAddress);
                        contactIdVSContactAddrressListMap.put(contactAddress.rC_Bios__Contact__r.Id, contactRelatedContactAddressList);
                    }
                }
                system.debug('=== contactIdVSContactAddrressListMap#### ' + contactIdVSContactAddrressListMap);
            }


            if(!contactIdVSContactAddrressListMap.isEmpty()) {
                for(Id contactId :contactIdVSContactAddrressListMap.keySet()) {
                    if(contactIdVSContactAddrressListMap.containsKey(contactId)) {
                        List<rC_Bios__Contact_Address__c> existingContactAddressList = contactIdVSContactAddrressListMap.get(contactId);

                        for(rC_Bios__Contact_Address__c contactAddress :existingContactAddressList)
                            if(contactAddress.rC_Bios__Address__r.rC_Bios__Unique_MD5__c != null)
                                oldUniqueAddressAndContactKeyVSContactAddressMap.put(contactAddress.rC_Bios__Address__r.rC_Bios__Unique_MD5__c+'_'+contactId, contactAddress);
                    }
                }
            }
            system.debug('=== oldUniqueAddressAndContactKeyVSContactAddressMap#### ' + oldUniqueAddressAndContactKeyVSContactAddressMap);

        }

        Map<Id, rC_Bios__Contact_Address__c> contactAddressToUpdateMap = new Map<Id, rC_Bios__Contact_Address__c>();
            Map<Id, rC_Bios__Contact_Address__c> contactAddressToInsertMap = new Map<Id, rC_Bios__Contact_Address__c>();

             List<rC_Bios__Contact_Address__c> contactAddressInsertList = new List<rC_Bios__Contact_Address__c>();
            List<rC_Bios__Contact_Address__c> contactAddressUpdateList = new List<rC_Bios__Contact_Address__c>();

        for(rC_Bios__Contact_Address__c newContactAddress : insertContactAddressList) {




            system.debug('contactIdVSUniqueAddressAndContactKeyMap ##############'+contactIdVSUniqueAddressAndContactKeyMap);

            if(!contactIdVSUniqueAddressAndContactKeyMap.isEmpty() && contactIdVSUniqueAddressAndContactKeyMap.containsKey(newContactAddress.rC_Bios__Contact__c)) {

                String addressUniqueKey = contactIdVSUniqueAddressAndContactKeyMap.get(newContactAddress.rC_Bios__Contact__c);
                system.debug('addressUniqueKey ##############'+addressUniqueKey);
                system.debug('oldUniqueAddressAndContactKeyVSContactAddressMap ##############'+oldUniqueAddressAndContactKeyVSContactAddressMap);

                if(!oldUniqueAddressAndContactKeyVSContactAddressMap.isEmpty() && oldUniqueAddressAndContactKeyVSContactAddressMap.containsKey(addressUniqueKey)) {
                    rC_Bios__Contact_Address__c preferredExistingContactAddress = oldUniqueAddressAndContactKeyVSContactAddressMap.get(addressUniqueKey);
                    system.debug('preferredExistingContactAddress ##############'+preferredExistingContactAddress);

                    if(!preferredExistingContactAddress.rC_Bios__Preferred_Mailing__c) {
                        system.debug('uniqueAddressAndContactKeyVSContactIdMap ##############'+uniqueAddressAndContactKeyVSContactIdMap);

                        if(!uniqueAddressAndContactKeyVSContactIdMap.isEmpty() && uniqueAddressAndContactKeyVSContactIdMap.containsKey(addressUniqueKey)) {
                            String contactId = uniqueAddressAndContactKeyVSContactIdMap.get(addressUniqueKey);
                            system.debug('contactId ##############'+contactId);
                            system.debug('contactIdVSContactAddrressListMap ##############'+contactIdVSContactAddrressListMap);

                            if(!contactIdVSContactAddrressListMap.isEmpty() && contactIdVSContactAddrressListMap.containsKey(contactId)) {
                                List<rC_Bios__Contact_Address__c> existingContactAddressList = contactIdVSContactAddrressListMap.get(contactId);
                                system.debug('existingContactAddressList ##############'+existingContactAddressList);

                                for(rC_Bios__Contact_Address__c existingContactAddress :existingContactAddressList) {
                                    if(existingContactAddress.Id != preferredExistingContactAddress.Id && existingContactAddress.rC_Bios__Preferred_Mailing__c) {
                                        existingContactAddress.rC_Bios__Preferred_Mailing__c = false;
                                        contactAddressToUpdateMap.put(existingContactAddress.Id, existingContactAddress);
                                    }

                                    if(existingContactAddress.Id == preferredExistingContactAddress.Id) {
                                        existingContactAddress.rC_Bios__Preferred_Mailing__c = true;
                                        contactAddressToUpdateMap.put(existingContactAddress.Id, existingContactAddress);
                                    }
                                }

                                system.debug('contactAddressToUpdateMap ##############'+contactAddressToUpdateMap);
                            }
                        }
                    }
                }

                else if(!uniqueAddressAndContactKeyVSContactAddressMap.isEmpty() && uniqueAddressAndContactKeyVSContactAddressMap.containsKey(addressUniqueKey)) {
                    rC_Bios__Contact_Address__c contactAddressNew = uniqueAddressAndContactKeyVSContactAddressMap.get(addressUniqueKey);
                    system.debug('11111contactAddressNew ##############'+contactAddressNew);
                    system.debug('uniqueAddressAndContactKeyVSContactIdMap ##############'+uniqueAddressAndContactKeyVSContactIdMap);

                    if(!uniqueAddressAndContactKeyVSContactIdMap.isEmpty() && uniqueAddressAndContactKeyVSContactIdMap.containsKey(addressUniqueKey)) {
                        String contactId = uniqueAddressAndContactKeyVSContactIdMap.get(addressUniqueKey);
                        system.debug('contactId ##############'+contactId);
                        system.debug('contactIdVSContactAddrressListMap ##############'+contactIdVSContactAddrressListMap);

                        if(!contactIdVSContactAddrressListMap.isEmpty() && contactIdVSContactAddrressListMap.containsKey(contactId)) {
                            List<rC_Bios__Contact_Address__c> existingContactAddressList = contactIdVSContactAddrressListMap.get(contactId);
                            system.debug('existingContactAddressList ##############'+existingContactAddressList);
                            Map<String, rC_Bios__Contact_Address__c> exstingContactAddressKeyVSContactAddress = new Map<String, rC_Bios__Contact_Address__c>();

                            for(rC_Bios__Contact_Address__c existingContactAddress :existingContactAddressList) {
                                if(existingContactAddress.rC_Bios__Preferred_Mailing__c) {
                                    existingContactAddress.rC_Bios__Preferred_Mailing__c = false;
                                    contactAddressToUpdateMap.put(existingContactAddress.Id, existingContactAddress);
                                    exstingContactAddressKeyVSContactAddress.put(existingContactAddress.rC_Bios__Address__r.rC_Bios__Unique_MD5__c, existingContactAddress);
                                }

                                if(existingContactAddress.Id == contactAddressNew.Id) {
                                    existingContactAddress.rC_Bios__Preferred_Mailing__c = true;
                                    contactAddressToInsertMap.put(existingContactAddress.Id, existingContactAddress);
                                    exstingContactAddressKeyVSContactAddress.put(existingContactAddress.rC_Bios__Address__r.rC_Bios__Unique_MD5__c, existingContactAddress);
                                }
                            }

                            if(!exstingContactAddressKeyVSContactAddress.isEmpty() && !exstingContactAddressKeyVSContactAddress.containsKey(addressUniqueKey)) {
                                system.debug('111#########'+contactId);
                                contactAddressInsertList.add(contactAddressNew);
                            }
                            if(exstingContactAddressKeyVSContactAddress.isEmpty()) {
                                system.debug('222#########'+contactId);
                                contactAddressNew.rC_Bios__Preferred_Mailing__c = true;
                                contactAddressInsertList.add(contactAddressNew);
                            }
                            system.debug('contactAddressInsertList #2222222222############'+contactAddressInsertList);
                        }

                        if(!contactIdVSContactAddrressListMap.isEmpty() && !contactIdVSContactAddrressListMap.containsKey(contactId)) {
                            contactAddressNew.rC_Bios__Preferred_Mailing__c = true;
                            contactAddressInsertList.add(contactAddressNew);
                        }


                        if(contactIdVSContactAddrressListMap.isEmpty()) {
                            //contactAddressToInsertMap.put(contactAddressNew.Id, contactAddressNew);
                            contactAddressNew.rC_Bios__Preferred_Mailing__c = true;
                            contactAddressInsertList.add(contactAddressNew);
                        }
                        system.debug('contactAddressInsertList ##############'+contactAddressInsertList);
                    }
                    system.debug('contactAddressToInsertMap ##############'+contactAddressToInsertMap);
                    system.debug('contactAddressToUpdateMap ##############'+contactAddressToUpdateMap);

                }
            }
        }
        Map<Id, rC_Bios__Contact_Address__c> contactAddressUpsertMap = new Map<Id, rC_Bios__Contact_Address__c>();

        if(!contactAddressToUpdateMap.isEmpty())
            for(Id contactAddressId : contactAddressToUpdateMap.keySet())
                contactAddressUpsertMap.put(contactAddressId, contactAddressToUpdateMap.get(contactAddressId));

        if(!contactAddressToInsertMap.isEmpty())
            for(Id contactAddressId : contactAddressToInsertMap.keySet())
                contactAddressUpsertMap.put(contactAddressId, contactAddressToInsertMap.get(contactAddressId));

        system.debug('***contactAddressUpsertMap********'+contactAddressUpsertMap);
        try{
            List<Database.UpsertResult> lstContactAddressUpsertResult;

            if(!contactAddressUpsertMap.isEmpty()) {
                lstContactAddressUpsertResult = database.upsert(contactAddressUpsertMap.values() , false);
                system.debug('***lstContactAddressUpsertResult**1111******'+lstContactAddressUpsertResult);
            }
            system.debug('***contactAddressInsertList********'+contactAddressInsertList);

            if(contactAddressInsertList != null && contactAddressInsertList.size() > 0 )
                lstContactAddressUpsertResult = database.upsert(contactAddressInsertList, false);

            system.debug('***lstContactAddressUpsertResult********'+lstContactAddressUpsertResult);

            if(lstContactAddressUpsertResult != null && lstContactAddressUpsertResult.size() > 0) {
                for(Database.UpsertResult upsertResult : lstContactAddressUpsertResult) {
                    if(upsertResult.isSuccess())
                        contactAddressIds.add(upsertResult.getId());
                }
            }
            system.debug('***contactAddressIds********'+contactAddressIds);

        }
        catch(Exception exception1) {
            system.debug('***exception********'+exception1);
        }
        return contactAddressIds;
        /*if(contactAdressMapToUpsert1 != null && contactAdressMapToUpsert1.values().size() > 0) {
            system.debug('########33333#########'+contactAdressMapToUpsert1);

            List<Database.UpsertResult> lstContactAddressUpsertResult;
            try {
                system.debug('**** before database.upsert');

                List<rC_Bios__Contact_Address__c> rC_BiosContactAddress55 = new List<rC_Bios__Contact_Address__c>();

                List<List<rC_Bios__Contact_Address__c>> upsertrC_BiosContact_AddressList = new List<List<rC_Bios__Contact_Address__c>>();
                upsertrC_BiosContact_AddressList.addAll(contactAdressMapToUpsert1.values());

                for(List<rC_Bios__Contact_Address__c>  objList :upsertrC_BiosContact_AddressList) {
                    for(rC_Bios__Contact_Address__c BiosContact_Address1 :objList ) {
                        rC_BiosContactAddress55.add(BiosContact_Address1);
                    }
                }

                if(!rC_BiosContactAddress55.isEmpty())
                    lstContactAddressUpsertResult = database.upsert(rC_BiosContactAddress55 , false);
                //database.upsert(pLIST<SObject>, pBoolean)

                system.debug('**** after database.upsert');
            } catch (Exception Ex) {
                system.debug('==== Contact Address Exception ===: ' + Ex.getMessage());
            }

            if(lstContactAddressUpsertResult != null && lstContactAddressUpsertResult.size() > 0) {
                for(Database.UpsertResult upsertResult : lstContactAddressUpsertResult) {
                    if(upsertResult.isSuccess())
                        contactAddressIds.add(upsertResult.getId());
                }
            }
        }*/



    }


}