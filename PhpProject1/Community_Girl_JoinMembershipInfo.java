public class Community_Girl_JoinMembershipInfo extends SobjectExtension{

    public String membershipProduct { get; set; }
    public String dateOfBirth { get; set; }
    public String girlPhone { get; set; }
    public String girlEmail { get; set; }
    public String selectedCustodialInfo { get; set; }
    public Boolean custodialFlag { get; set; }
    public String councilServiceFee { get; set; }
    public String streetLine1 { get; set; }
    public String streetLine2 { get; set; }
    public String city { get; set; }
    public String state { get; set; }
    public String zipCode { get; set; }
    public String county { get; set; }
    public String country { get; set; }
    public boolean emailOptIn {get;set;}
    public boolean textOptIn {get;set;}
    public String primaryFirstName {get;set;}
    public String secondaryFirstName {get;set;}
    public String primaryLastName {get;set;}
    public String secondaryLastName {get;set;}
    public String primaryEmail { get; set; }
    public String secondaryEmail { get; set; }
    public String primaryEmail2 { get; set; }
    public String secondaryEmail2 { get; set; }
    public String primaryPreferredEmail { get; set; }
    public String secondaryPreferredEmail { get; set; }
    public String primaryGender { get; set; }
    public String secondaryGender { get; set; }
    public String primaryHomePhone { get; set; }
    public String secondaryHomePhone { get; set; }
    public String primaryBusinessPhone { get; set; }
    public String secondaryBusinessPhone { get; set; }
    public String primaryMobilePhone { get; set; }
    public String secondaryMobilePhone { get; set; }
    public String primaryPreferredPhone { get; set; }
    public String secondaryPreferredPhone { get; set; }

    public static String parentContactId;
    public static String girlContactId;

    public String primaryStreetLine1 { get; set; }
    public String secondaryStreetLine1 { get; set; }
    public String primaryStreetLine2 { get; set; }
    public String secondaryStreetLine2 { get; set; }
    public String primaryCity { get; set; }
    public String secondaryCity { get; set; }
    public String primaryState { get; set; }
    public String secondaryState { get; set; }
    public String primaryZipCode { get; set; }
    public String secondaryZipCode { get; set; }
    public String primaryCounty { get; set; }
    public String secondaryCounty  { get; set; }
    public String primaryCountry { get; set; }
    public String secondaryCountry  { get; set; }
    public boolean primaryEmailOptIn {get;set;}
    public boolean secondaryEmailOptIn {get;set;}
    public boolean primaryTextOptIn {get;set;}
    public boolean secondaryTextOptIn {get;set;}
    public Boolean booleanContactPhotoOptIn{ get; set; }
    public Boolean booleanOppGrantRequested{ get; set; }
    public Boolean booleanGrantRequested{ get; set; }
    public Boolean booleanOpportunityGrantRequested{ get; set; }

    public Account councilAccount{ get; set; }
    public Boolean isCouncilTermsConditionsAvailable{get;set;}
    public Boolean booleanContactEmailOptIn{ get; set; }
    public Boolean booleanContactTextPhoneOptIn{ get; set; }
    public Boolean financialCheckBox{get;set;}
    public Boolean booleanTermsAndConditions{ get; set; }
    public Boolean booleanOppMembershipOnPaper{ get; set; }
    public Boolean tremsflag { get; set; }
    public boolean isGirlAbove13 {get;set;}
   // public boolean statehideflag {get;set;}

    public String firstName{ get; set; }
    public String lastName { get; set; }
    public String preferredEmail{ get; set; }
    public String homePhone{ get; set; }
    public String email{ get; set; }
    public String gender{ get; set; }
    public String preferredPhone{ get; set; }
    public String mobilePhone{ get; set; }
    public String termsAndCondition { get; set; }

    private Contact contact;
    private Contact parentContact;
    private Contact girlContact;
    private Contact secondaryContact;
    private PricebookEntry[] PricebookEntryList;
    private map<Id, PricebookEntry> priceBookEntryMap;
    private Zip_Code__c matchingZipCode = new Zip_Code__c();
    private String councilId;
    private String contactId;
    String errorMessage = '';
    private rC_Bios__Address__c parentAddress;
    private rC_Bios__Address__c girlAddress;
    private rC_Bios__Address__c secondaryAddress;
    
    private List<Contact> contacts;
    private User systemAdminUser;
    Double totalPrice = 0.00;
    private static Integer counterUnableToLockRow = 0;
    public static final string LIFETIME_MEMBERSHIP  = 'Lifetime Membership';
    
    private Opportunity opportunityServicefee;
    private string oldCampaign = '';
    public Community_Girl_JoinMembershipInfo() {
        booleanOppMembershipOnPaper = false;
        booleanOpportunityGrantRequested = false;
        isGirlAbove13 = false;
       // statehideflag =false;
        tremsflag = false;
        custodialFlag = false;
        booleanTermsAndConditions = false;
        booleanContactPhotoOptIn = true;
        booleanOppGrantRequested = false;
        emailOptIn = false;
        textOptIn = false;
        totalPrice = 0.00;
        counterUnableToLockRow = 0;
        //termsAndCondition = 'I/we accept and abide by the <a href="http://www.girlscouts.org/program/basics/promise_law/">Girl Scout Promise and Law</a>.';
        priceBookEntryMap = new map<Id, PricebookEntry>();
        PricebookEntryList = new PricebookEntry[]{};
        fillPricebookEntryList();
        councilAccount = new Account();
        
        if(Apexpages.currentPage().getParameters().containsKey('ParentContactId'))
            parentContactId = Apexpages.currentPage().getParameters().get('ParentContactId');
        if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            girlContactId = Apexpages.currentPage().getParameters().get('GirlContactId');
        if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');
        if(Apexpages.currentPage().getParameters().containsKey('CouncilId'))
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
                    councilServiceFee = 'Council Service Fee $' + Girl_RegistrationHeaderController.councilAccount.Service_Fee__c; 
                else
                    councilServiceFee = 'Council Service Fee $0.00' ;   
            }
        }
                
        if(girlContactId != null && girlContactId != '')
            girlContact = getContact(girlContactId);

        if(girlContact != null && girlContact.Id != null && girlContact.MailingPostalCode != null)
            zipCode = girlContact.MailingPostalCode;

        if(parentContactId != null && parentContactId != '')
            parentContact = getContact(parentContactId);
        
        // getting address from rc_Bios
        parentAddress = new rC_Bios__Address__c();
        girlAddress = new rC_Bios__Address__c();
        if(parentContact.rC_Bios__Preferred_Mailing_Address__c!=null)
        parentAddress = [Select Id,rC_Bios__City__c,rC_Bios__Country__c,rC_Bios__County__c,rC_Bios__Postal_Code__c,rC_Bios__State__c,rC_Bios__Street_Line_1__c,rC_Bios__Street_Line_2__c from rC_Bios__Address__c where ID = :parentContact.rC_Bios__Preferred_Mailing_Address__c];
        if(girlContact.rC_Bios__Preferred_Mailing_Address__c!=null)
        girlAddress = [Select Id,rC_Bios__City__c,rC_Bios__Country__c,rC_Bios__County__c,rC_Bios__Postal_Code__c,rC_Bios__State__c,rC_Bios__Street_Line_1__c,rC_Bios__Street_Line_2__c from rC_Bios__Address__c where ID = :girlContact.rC_Bios__Preferred_Mailing_Address__c];
        
        if(parentContact != null) {
            systemAdminUser = [Select Id
                 , LastName
                 , IsActive
                 , Profile.Name
                 , Profile.Id
                 , ProfileId  from User where Id = :parentContact.Account.OwnerId
               and IsActive = true 
               and UserRoleId != null
            limit 1];
            primaryFirstName = parentContact.FirstName;
            primaryLastName = parentContact.LastName;
            primaryEmail = parentContact.rC_Bios__Home_Email__c;            
            primaryZipCode = parentContact.MailingPostalCode;
            primaryHomePhone = parentContact.HomePhone;
            primaryBusinessPhone = parentContact.rC_Bios__Work_Phone__c; 
            
            primaryPreferredEmail = (parentContact.rC_Bios__Preferred_Email__c.equalsIgnoreCase('Home'))
                                    ? 'Email'
                                    : (parentContact.rC_Bios__Preferred_Email__c.equalsIgnoreCase('Work')) ? 'Email 2' : '';

            if(parentContact.rC_Bios__Preferred_Phone__c != null && parentContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Home'))
                primaryPreferredPhone = 'Home Phone';
            if(parentContact.rC_Bios__Preferred_Phone__c != null && parentContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Work'))
                primaryPreferredPhone = 'Business Phone';
            if(parentContact.rC_Bios__Preferred_Phone__c != null && parentContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Mobile'))
                primaryPreferredPhone = 'Mobile Phone';
                
            primaryMobilePhone = parentContact.MobilePhone;
            primaryEmail2 = parentContact.rC_Bios__Work_Email__c;
            primaryTextOptIn = parentContact.Text_Phone_Opt_In__c;
            primaryEmailOptIn = parentContact.Email_Opt_In__c;
            primaryGender = parentContact.rC_Bios__Gender__c;
            
       }
       if(parentAddress != null) {
            primaryStreetLine1 = parentAddress.rC_Bios__Street_Line_1__c;
            primaryStreetLine2 = parentAddress.rC_Bios__Street_Line_2__c;
            primaryCity = parentAddress.rC_Bios__City__c;
            primaryState= parentAddress.rC_Bios__State__c;
            primaryCountry  = parentAddress.rC_Bios__Country__c;
            primaryZipCode = parentAddress.rC_Bios__Postal_Code__c;
            primaryCounty = parentAddress.rC_Bios__County__c;
       }
       if(girlContact != null) {            
            streetLine1 = girlContact.MailingStreet;
            city = girlContact.MailingCity;
            state = girlContact.MailingState;
            country = girlContact.MailingCountry;
            girlEmail = girlContact.Email;
            girlPhone = girlContact.Phone;
            selectedCustodialInfo = girlContact.Custodial_Care__c;
            
            dateOfBirth = girlContact.rC_Bios__Birth_Month__c + '/' + girlContact.rC_Bios__Birth_Day__c + '/' + girlContact.rC_Bios__Birth_Year__c;
            
            textOptIn = parentContact.Text_Phone_Opt_In__c;            
            emailOptIn = parentContact.Email_Opt_In__c;
       }
       if(girlAddress != null) {
            streetLine1 = girlAddress.rC_Bios__Street_Line_1__c;
            streetLine2 = girlAddress.rC_Bios__Street_Line_2__c;
            city = girlAddress.rC_Bios__City__c;
            state= girlAddress.rC_Bios__State__c;
            country  = girlAddress.rC_Bios__Country__c;
            zipCode = girlAddress.rC_Bios__Postal_Code__c;
            county = girlAddress.rC_Bios__County__c;             
       }
       // Populating Secondary contact                 
        Contact con = new contact();
        contacts = new List<contact>();
        con = [Select AccountID,createdDate from contact where ID = :parentContact.ID and Account.RecordType.Name = 'Household'];
        if(con.AccountID != NULL){
            contacts = [Select ID from contact where AccountID = :con.AccountID and rC_Bios__Role__c = 'Adult' and ID != :parentContact.ID and createdDate >= :con.createdDate order by CreatedDate Desc];
        }
       if(contacts.size()>0) {       
           system.debug('contacts[0].ID... '+contacts[0].ID);
           string conatctID = contacts[0].ID;
           secondaryAddress = new rC_Bios__Address__c();
           if(conatctID != null && conatctID != '')
               secondaryContact = getContact(conatctID);
           secondaryAddress = new rC_Bios__Address__c();
           system.debug('secondaryContact... '+secondaryContact);
           if(secondaryContact.rC_Bios__Preferred_Mailing_Address__c != null){ 
               secondaryAddress = [Select Id,rC_Bios__City__c,rC_Bios__Country__c,rC_Bios__County__c,rC_Bios__Postal_Code__c,rC_Bios__State__c,rC_Bios__Street_Line_1__c,rC_Bios__Street_Line_2__c from rC_Bios__Address__c where ID = :secondaryContact.rC_Bios__Preferred_Mailing_Address__c];
           }
           system.debug('secondaryAddress... '+secondaryAddress);
           if(secondaryContact != null) {
                secondaryFirstName = secondaryContact.FirstName;
                secondaryLastName = secondaryContact.LastName;
                secondaryEmail = secondaryContact.rC_Bios__Home_Email__c;            
                secondaryZipCode = secondaryContact.MailingPostalCode;
                secondaryHomePhone = secondaryContact.HomePhone;
                secondaryBusinessPhone = secondaryContact.rC_Bios__Work_Phone__c; 
                
                secondaryPreferredEmail = (secondaryContact.rC_Bios__Preferred_Email__c.equalsIgnoreCase('Home'))
                                    ? 'Email'
                                    : (secondaryContact.rC_Bios__Preferred_Email__c.equalsIgnoreCase('Work')) ? 'Email 2' : '';

                if(secondaryContact.rC_Bios__Preferred_Phone__c != null && secondaryContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Home'))
                secondaryPreferredPhone = 'Home Phone';
                if(secondaryContact.rC_Bios__Preferred_Phone__c != null && secondaryContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Work'))
                secondaryPreferredPhone = 'Business Phone';
                if(secondaryContact.rC_Bios__Preferred_Phone__c != null && secondaryContact.rC_Bios__Preferred_Phone__c.equalsIgnoreCase('Mobile'))
                secondaryPreferredPhone = 'Mobile Phone';
             
                
                secondaryMobilePhone = secondaryContact.MobilePhone;
                secondaryEmail2 = secondaryContact.rC_Bios__Work_Email__c;
                secondaryTextOptIn = secondaryContact.Text_Phone_Opt_In__c;
                secondaryEmailOptIn = secondaryContact.Email_Opt_In__c;     
                secondaryGender = secondaryContact.rC_Bios__Gender__c;           
           }
           if(secondaryAddress != null) {
                secondaryStreetLine1 = secondaryAddress.rC_Bios__Street_Line_1__c;
                secondaryStreetLine2 = secondaryAddress.rC_Bios__Street_Line_2__c;
                secondaryCity = secondaryAddress.rC_Bios__City__c;
                secondaryState= secondaryAddress.rC_Bios__State__c;
                secondaryCountry  = secondaryAddress.rC_Bios__Country__c;
                secondaryZipCode = secondaryAddress.rC_Bios__Postal_Code__c;
                secondaryCounty = secondaryAddress.rC_Bios__County__c;
           }
       } 
       else {
           secondaryContact = new Contact();
       }
       calculateGirlAge();
       if(selectedCustodialInfo !='' && selectedCustodialInfo !=null)
       showSecondaryContact();
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
        return errorMessage;
    }

    public pageReference calculateGirlAge() {
        Date birthDate;
        try {
            if(dateOfBirth != null && dateOfBirth != '' && dateOfBirth.contains('/')) {
                String[] strBirthDate = dateOfBirth.trim().split('/');
                birthDate = Date.newInstance(Integer.valueof(strBirthDate[2].trim()), Integer.valueof(strBirthDate[0].trim()), Integer.valueof(strBirthDate[1].trim()));
            }

            if(birthDate != null)
                isGirlAbove13 = math.floor((birthDate.daysBetween(Date.Today()))/365.25) >= 13 ? true : false;
        } catch(System.exception pException) {
            
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
                 , Account.OwnerId
                 , Account.rC_Bios__Preferred_Contact__c
                 , rC_Bios__Home_Email__c
                 , rC_Bios__Gender__c
                 , rC_Bios__Preferred_Email__c
                 , rC_Bios__Preferred_Phone__c
                 , rC_Bios__Work_Phone__c
                 , MailingPostalCode
                 , HomePhone 
                 , MobilePhone
                 , MailingStreet
                 , MailingCity
                 , MailingState
                 , MailingCountry
                 , Phone
                 , Email
                 , rC_Bios__Preferred_Mailing_Address__c
                 , rC_Bios__Work_Email__c
                 , Text_Phone_Opt_In__c
                 , Email_Opt_In__c
                 , Custodial_Care__c
                 , rC_Bios__Birth_Day__c
                 , rC_Bios__Birth_Month__c
                 , rC_Bios__Birth_Year__c
                 , Account.rC_Bios__Preferred_Contact__r.email
                 , rC_Bios__Role__c

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

            if(primaryPreferredEmail != null && primaryPreferredEmail != '') {
                if(primaryPreferredEmail.equalsIgnoreCase('Email'))
                    contactToUpdate.rC_Bios__Home_Email__c = primaryEmail;
                if(primaryPreferredEmail.equalsIgnoreCase('Email2'))
                    contactToUpdate.rC_Bios__Work_Email__c = (primaryEmail2 != null && primaryEmail2 != '') ? primaryEmail2 : primaryEmail;
            }

            contactToUpdate.HomePhone = (primaryHomePhone != null && primaryHomePhone.length() > 20) ? primaryHomePhone.substring(0, 20) : primaryHomePhone;
            contactToUpdate.rC_Bios__Work_Phone__c = (primaryBusinessPhone != null && primaryBusinessPhone.length() > 20) ? primaryBusinessPhone.substring(0, 20) : primaryBusinessPhone;

            
             if(primaryPreferredPhone != null && primaryPreferredPhone != ''){

                if(primaryPreferredPhone.equalsIgnoreCase('Home Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Home';
                }

                if(primaryPreferredPhone.equalsIgnoreCase('Business Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Work';
                }

                if(primaryPreferredPhone.equalsIgnoreCase('Mobile Phone')){
                    contactToUpdate.rC_Bios__Preferred_Phone__c = 'Mobile';                   
                }
            }
            contactToUpdate.Email_Opt_In__c = primaryEmailOptIn;
            contactToUpdate.MobilePhone = (primaryMobilePhone != null && primaryMobilePhone.length() > 20) ? primaryMobilePhone.substring(0, 20) : primaryMobilePhone;
            contactToUpdate.Text_Phone_Opt_In__c = primaryTextOptIn;
            contactToUpdate.Photo_Opt_In__c = booleanContactPhotoOptIn;
            update contactToUpdate;
            return contactToUpdate;
        }
        return null;
    }
    
    public Contact updateGirlContact(Contact contactToUpdate) {
        if(contactToUpdate != null && contactToUpdate.Id != null) {
            Date birthDate;
            system.debug('== birthDate ==: ' + birthDate + ' == dateOfBirth ==: ' + dateOfBirth);
            if(dateOfBirth.contains('/')) {
                String[] strBirthDate = dateOfBirth.trim().split('/');
                birthDate = Date.newInstance(Integer.valueof(strBirthDate[2].trim()), Integer.valueof(strBirthDate[0].trim()), Integer.valueof(strBirthDate[1].trim()));
                contactToUpdate.rC_Bios__Birth_Day__c = strBirthDate[1].trim();
                contactToUpdate.rC_Bios__Birth_Month__c = strBirthDate[0].trim();
                contactToUpdate.rC_Bios__Birth_Year__c = strBirthDate[2].trim();
            }
            
            //contactToUpdate.Birthdate = birthDate;
            contactToUpdate.rC_Bios__Preferred_Email__c = 'Home';
            contactToUpdate.rC_Bios__Preferred_Phone__c = 'Home';
            contactToUpdate.rC_Bios__Home_Email__c = (girlEmail != null) ? girlEmail : primaryEmail;
            contactToUpdate.HomePhone = (girlPhone != null && girlPhone.length() > 20) ? girlPhone.substring(0, 20) : girlPhone;
            contactToUpdate.MailingPostalCode = zipCode;
            contactToUpdate.Email_Opt_In__c = emailOptIn;
            contactToUpdate.Text_Phone_Opt_In__c = textOptIn;
            contactToUpdate.Custodial_Care__c = selectedCustodialInfo;
            contactToUpdate.Photo_Opt_In__c = booleanContactPhotoOptIn;
            
            update contactToUpdate;
            system.debug('== 1. girlContact ==: ' + [select Id, FirstName, LastName, Birthdate, Email from Contact where Id =: contactToUpdate.Id]);
            return contactToUpdate;
        }
        return null;
    }
    public Contact updateSecondaryContact() {
            system.debug('===inside==>');
            secondaryContact.FirstName = (secondaryFirstName != null && secondaryFirstName  != '' && secondaryFirstName.length() > 40) ? secondaryFirstName.substring(0, 40) : secondaryFirstName;
            secondaryContact.LastName = (secondaryLastName != null && secondaryLastName != '' && secondaryLastName.length() > 80) ? secondaryLastName.substring(0, 80) : secondaryLastName;
            //secondaryContact.AccountId = primaryContact.AccountId;
            secondaryContact.rC_Bios__Home_Email__c = secondaryEmail;
            secondaryContact.rC_Bios__Work_Email__c = secondaryEmail2; 
            secondaryContact.rC_Bios__Gender__c = (secondaryGender != null && secondaryGender != '' && secondaryGender.toUpperCase().contains('NONE')) ? '' : secondaryGender;
            secondaryContact.rC_Bios__Role__c = 'Adult';
            secondaryContact.HomePhone = secondaryHomePhone;
            secondaryContact.Email_Opt_In__c = secondaryEmailOptIn;
            secondaryContact.Text_Phone_Opt_In__c = secondaryTextOptIn;
            secondaryContact.Photo_Opt_In__c = booleanContactPhotoOptIn;
            
            system.debug('=11====>'+secondaryContact);
            
            if(secondaryPreferredEmail != null && secondaryPreferredEmail != '') {
                secondaryContact.rC_Bios__Preferred_Email__c = secondaryPreferredEmail.toUpperCase().contains('NONE') ? '' 
                                                               : (secondaryPreferredEmail.equalsIgnoreCase('Email') ? 'Home' : 'Work');
            }
            system.debug('=22====>'+secondaryContact);
            secondaryContact.rC_Bios__Work_Phone__c = (secondaryBusinessPhone != null && secondaryBusinessPhone  != '' && secondaryBusinessPhone.length() > 20) ? secondaryBusinessPhone.substring(0, 20) 
                                                      : secondaryBusinessPhone;

            if(secondaryPreferredPhone != null && secondaryPreferredPhone != ''){

                if(secondaryPreferredPhone.equalsIgnoreCase('Home Phone')){
                    secondaryContact.rC_Bios__Preferred_Phone__c = 'Home';
                }

                if(secondaryPreferredPhone.equalsIgnoreCase('Business Phone')){
                    secondaryContact.rC_Bios__Preferred_Phone__c = 'Work';
                }

                if(secondaryPreferredPhone.equalsIgnoreCase('Mobile Phone')){
                    secondaryContact.rC_Bios__Preferred_Phone__c = 'Mobile';                   
                }
            }
            
            system.debug('=333====>'+secondaryContact);
            
            secondaryContact.MobilePhone = (secondaryMobilePhone != null && secondaryMobilePhone != '' && secondaryMobilePhone.length() > 20) ? secondaryMobilePhone.substring(0, 20) : secondaryMobilePhone;
            system.debug('=44===>'+secondaryContact);
        return secondaryContact;
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
            secondaryContact.rC_Bios__Home_Email__c = secondaryEmail;
            secondaryContact.rC_Bios__Work_Email__c = secondaryEmail2; 
            secondaryContact.rC_Bios__Gender__c = (secondaryGender != null && secondaryGender != '' && secondaryGender.toUpperCase().contains('NONE')) ? '' : secondaryGender;
            secondaryContact.rC_Bios__Role__c = 'Adult';
            secondaryContact.HomePhone = secondaryHomePhone;
            secondaryContact.Email_Opt_In__c = secondaryEmailOptIn;
            secondaryContact.Text_Phone_Opt_In__c = secondaryTextOptIn;
            secondaryContact.Photo_Opt_In__c = booleanContactPhotoOptIn;
            
            system.debug('=11====>'+secondaryContact);
            
            if(secondaryPreferredEmail != null && secondaryPreferredEmail != '') {
                secondaryContact.rC_Bios__Preferred_Email__c = secondaryPreferredEmail.toUpperCase().contains('NONE') ? '' 
                                                               : (secondaryPreferredEmail.equalsIgnoreCase('Email') ? 'Home' : 'Work');
            }
            system.debug('=22====>'+secondaryContact);
            secondaryContact.rC_Bios__Work_Phone__c = (secondaryBusinessPhone != null && secondaryBusinessPhone  != '' && secondaryBusinessPhone.length() > 20) ? secondaryBusinessPhone.substring(0, 20) 
                                                      : secondaryBusinessPhone;

            if(secondaryPreferredPhone != null && secondaryPreferredPhone != '')
                secondaryContact.rC_Bios__Preferred_Phone__c = secondaryPreferredPhone.toUpperCase().contains('NONE') ? '' 
                                                               : ((secondaryPreferredPhone.equalsIgnoreCase('Business Phone')) ? 'Work' : secondaryPreferredPhone);
            system.debug('=333====>'+secondaryContact);
            
            secondaryContact.MobilePhone = (secondaryMobilePhone != null && secondaryMobilePhone != '' && secondaryMobilePhone.length() > 20) ? secondaryMobilePhone.substring(0, 20) : secondaryMobilePhone;
            system.debug('=44===>'+secondaryContact);
            String zipCodeToMatchSecondary = (secondaryZipCode != null && secondaryZipCode.length() > 5) ? secondaryZipCode.substring(0, 5) + '%' : secondaryZipCode+ '%';
            if(secondaryZipCode != null && secondaryZipCode != ''){
                zipCodeList = [
                    Select Id
                         , Name
                         , Council__c
                         , Zip_Code_Unique__c
                         , City__c
                         , Recruiter__c 
                      From Zip_Code__c 
                     where Zip_Code_Unique__c like :zipCodeToMatchSecondary limit 1
                ];
                system.debug('=55==>'+zipCodeList);
                
                secondaryZipCodeRecord = (zipCodeList != null && zipCodeList.size() > 0) ? zipCodeList[0] : new Zip_Code__c();
                system.debug('=66===>'+secondaryZipCodeRecord);
                
                
                if(secondaryZipCodeRecord != null && secondaryZipCodeRecord.Id != null && secondaryZipCodeRecord.Recruiter__c != null){
                    system.debug('***secondaryContact======***'+secondaryContact);
                    system.debug('***secondaryZipCodeRecord.Recruiter__c======***'+secondaryZipCodeRecord.Recruiter__c);
                    secondaryContact = GirlRegistrationUtilty.upsertContactOwner(secondaryContact, secondaryZipCodeRecord.Recruiter__c);
                }else
                    secondaryContact = GirlRegistrationUtilty.upsertContactOwner(secondaryContact, systemAdminUser.Id);

                system.debug('=77===>'+secondaryContact);
                
                //if(secondaryContact != null)
                //insert secondaryContact;
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
                 , Product2.rC_Giving__End_Date__c
                 , Product2.Name
              From PricebookEntry 
             where Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
               and IsActive = true
        ];
 
        if(PricebookEntryList != null && PricebookEntryList.size() > 0) {
            for(PricebookEntry pricebookEntry : PricebookEntryList)
                priceBookEntryMap.put(pricebookEntry.Id, pricebookEntry);
        }
    }
    
     public List<SelectOption> getcustodialCareInfo() {
        List<SelectOption> custodialCareInfo = new List<SelectOption>();
        custodialCareInfo.add(new Selectoption('--None--', '--None--'));
        custodialCareInfo.add(new SelectOption('Both Parents', 'Both Parents'));
        custodialCareInfo.add(new SelectOption('Parent and Secondary Contact', 'Parent and Secondary Contact'));
        custodialCareInfo.add(new SelectOption('Parent', 'Parent')); 
        custodialCareInfo.add(new SelectOption('Guardian', 'Guardian'));
        custodialCareInfo.add(new SelectOption('Guardian and Secondary Contact', 'Guardian and Secondary Contact'));
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
        //countryOptions.add(new SelectOption('--None--', '--None--'));
        countryOptions.add(new SelectOption('USA', 'USA'));
        //countryOptions.add(new SelectOption('IND', 'India'));
        //countryOptions.add(new SelectOption('CHN', 'China'));
        //countryOptions.add(new SelectOption('MEX', 'Mexico'));
        //countryOptions.add(new SelectOption('CAN', 'Canada'));
        //countryOptions.add(new SelectOption('OTR', 'OTHER'));
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
            if(zipCode != null && zipCode != '')
                primaryZipCode = zipCode;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }
    
    public PageReference secondaryAddressSave() {
        Savepoint savepoint = Database.setSavepoint();
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
            if(zipCode != null && zipCode != '')
                secondaryZipCode = zipCode;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }
    
    public PageReference submit() {        
        counterUnableToLockRow++;
        Savepoint savepoint = Database.setSavepoint();

        //Create G Email template Content
        
        string sCouncil_Header_Urlc;
        string sService_Feec;
        string sAdult_Contact_First_Namec='';
        string sGirl_First_Namec='';
        string sCouncil_Namec='';
        string sCouncil_Addressc='';
        string sContactrName='';
        string sAuto_Givingc='';
        string srC_GivingGiving_Amountc='';
        string sOwnerName='';
        string sOwner_Titlec='';
        string sOwner_Phonec='';
        string sOwner_Emailc='';
        
        try {
        Opportunity newOpportunity;
        OpportunityLineItem opportunityLineItem;
        PricebookEntry priceBookEntry;
        Account account;
        CampaignMember[] campaignMemberList;

        errorMessage = validateRequiredFields();
        if(errorMessage != null && errorMessage != '')
            return addErrorMessage(errorMessage);
        
        if(primaryPreferredEmail.equalsIgnoreCase('Email 2') && primaryEmail2 == '') {
             return addErrorMessage('Email 2 cannot be left blank when selecting Email 2 as Preferred.');
        }
        
        if(primaryPreferredPhone.equalsIgnoreCase('Business Phone') && primaryBusinessPhone == '') {
             return addErrorMessage('Business Phone cannot be left blank when selecting Business Phone as Preferred.');
        }
        if(primaryPreferredPhone.equalsIgnoreCase('Mobile Phone') && primaryMobilePhone == '') {
             return addErrorMessage('Mobile Phone cannot be left blank when selecting Mobile Phone as Preferred.');
        }
        if(secondaryPreferredEmail!=null){
        if(secondaryPreferredEmail.equalsIgnoreCase('Email 2') && secondaryEmail2 == '') {
             return addErrorMessage('Email 2 cannot be left blank when selecting Email 2 as Preferred.');
        }
        }
        if(secondaryPreferredPhone!=null){
        if(secondaryPreferredPhone.equalsIgnoreCase('Business Phone') && secondaryBusinessPhone == '') {
             return addErrorMessage('Business Phone cannot be left blank when selecting Business Phone as Preferred.');
        }
        if(secondaryPreferredPhone.equalsIgnoreCase('Mobile Phone') && secondaryMobilePhone == '') {
             return addErrorMessage('Mobile Phone cannot be left blank when selecting Mobile Phone as Preferred.');
        }
        }
        if(booleanTermsAndConditions == false)
            return addErrorMessage('To Proceed, You must agree to accept and abide by the Girl Scout Promise and Law.');

        
            Zip_Code__c[] zipCodeList = new Zip_Code__c[] {};
            system.debug('zipCode... '+zipCode);
            String zipCodeToMatch = (zipCode != null && zipCode.length() > 5) ? zipCode.substring(0, 5) + '%' : zipCode + '%';
            system.debug('zipCode... '+zipCode);
            if(zipCodeToMatch != null && zipCodeToMatch != '')
                zipCodeList = [
                    Select Id
                         , Name
                         , Council__c
                         , Zip_Code_Unique__c
                         , City__c
                         , Recruiter__c 
                      From Zip_Code__c 
                     where Zip_Code_Unique__c like :zipCodeToMatch and Recruiter__r.isActive = true limit 1
                ];
            
            matchingZipCode = (zipCodeList.size() > 0) ? zipCodeList[0] : new Zip_Code__c();
            system.debug('zipCodeList==='+zipCodeList);
            Set<String> campaignMemberIdSet = new Set<String>();
            String campaignMembers;
            String[] campaignMemberIdList;
            set<Id> ContactAddressIdSet = new set<Id>();
            List<rC_Bios__Contact_Address__c> insertContactAddressList = new List<rC_Bios__Contact_Address__c>();
            List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
            
            if(Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds'))
                campaignMembers = Apexpages.currentPage().getParameters().get('CampaignMemberIds');

            if(campaignMembers != null && campaignMembers != '') {
                campaignMemberIdList = campaignMembers.trim().split(',');
                oldCampaign = campaignMemberIdList[campaignMemberIdList.size()-1];
                for(String campaignMember : campaignMemberIdList)
                    campaignMemberIdSet.add(campaignMember.trim());
            }
            if (!Test.isRunningTest()) { 
            if(priceBookEntryMap != null && priceBookEntryMap.size() > 0) {
                if(membershipProduct != null && !membershipProduct.toUpperCase().contains('NONE') && priceBookEntryMap.containsKey(membershipProduct.trim()))
                    priceBookEntry = priceBookEntryMap.get(membershipProduct.trim());
            }
            
            if(girlContact == null || priceBookEntry == null) 
                return addErrorMessage('No girl contact found.');
            }
            // Added to avoid duplicate membership
            string membershipYear = '';
            if(priceBookEntry != null && priceBookEntry.Product2.rC_Giving__End_Date__c != null)
            membershipYear = string.valueOf(priceBookEntry.Product2.rC_Giving__End_Date__c.year());
            if(membershipYear !=null && membershipYear != '') {
            List<campaignmember> lstCM = [Select Membership__r.Membership_year__c from campaignmember where ContactId =:girlContact.ID ];// and Active__c=true];
            if(lstCM.size()>0) {
            for(campaignmember cm:lstCM) {
            if(cm.Membership__r.Membership_year__c == membershipYear)
            return addErrorMessageAndRollback(savepoint,'This membership is already active, Please select another membership.');
           // return addErrorMessage('This membership is already active, Please select another membership.');
            }
            }
            }
            //zipCode = (girlContact.MailingPostalCode != null) ? girlContact.MailingPostalCode : zipCode;
            system.debug('zipCode==='+zipCode);
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
            account = (accountList != null && accountList.size() > 0) ? accountList[0] : null;
            system.debug('accountList==='+accountList);
            parentContact = updatePrimaryContact(parentContact);
            girlContact = updateGirlContact(girlContact);
            system.debug('== girlContact ==: ' + [select Id, Birthdate from Contact where Id =: girlContact.Id]);
            set<String> contactAddressIds = new set<String>();
            if(custodialFlag == true){
            if(secondaryContact.id == null) {
                      
                    //======================= checking new secondary contact exists or not in same account========================================
                        if(secondaryFirstName!=null && secondaryFirstName!='' && secondaryLastName!=null && secondaryLastName !='' ){
                                    secondaryFirstName =secondaryFirstName.Trim();
                                    secondaryLastName=secondaryLastName.Trim();
                                    String sencondryconemail;
                                    String prefered=(secondaryPreferredEmail.toUpperCase().contains('NONE'))? '' : (secondaryPreferredEmail.equalsIgnoreCase('Email') ? 'Home' : 'Work');
                                        if(prefered=='Home'){
                                        sencondryconemail= secondaryEmail.Trim();
                                        }
                                        if(prefered=='Work'){
                                        sencondryconemail=secondaryEmail2.Trim();
                                        }
                                         system.debug('=====>girlContact.AccountId:'+girlContact.AccountId);
                                     List<Contact> duplicatecontactList = [
                                        Select Id
                                         , FirstName
                                         , LastName
                                         , Email
                                         , Phone
                                         , AccountId
                                         , Account.RecordType.Name
                                         , rC_Bios__Secondary_Contact__c
                                          ,rC_Bios__Preferred_Mailing_Address__c
                                         , Secondary_Role__c
                                      From Contact
                                     where FirstName = :secondaryFirstName
                                       And LastName = :secondaryLastName
                                       And Email = :sencondryconemail
                                       And rC_Bios__Role__c = :'Adult'
                                       And Account.RecordType.Name = :'Household'
                                       And AccountId= :girlContact.AccountId
                                  order by CreatedDate asc
                                  ];
                                 

                                 if(duplicatecontactList != null && duplicatecontactList.size() > 0){
                                    //if duplicate secondary contact exists========================================================================
                                       /*     rC_Bios__Address__c secondaryAddress3;
                                            system.debug('=====>duplicatecontactList[0]:'+duplicatecontactList[0]);
                                            system.debug('=====>secondarypreferedAddress :'+duplicatecontactList[0].rC_Bios__Preferred_Mailing_Address__c);
                                          if(duplicatecontactList[0].rC_Bios__Preferred_Mailing_Address__c != null){ 
                                               secondaryAddress3 = [Select Id,rC_Bios__City__c,rC_Bios__Country__c,rC_Bios__County__c,rC_Bios__Postal_Code__c,rC_Bios__State__c,rC_Bios__Street_Line_1__c,rC_Bios__Street_Line_2__c from rC_Bios__Address__c where ID = :duplicatecontactList[0].rC_Bios__Preferred_Mailing_Address__c];
                                           }
                                           system.debug('=====>secondaryAddress3 :'+secondaryAddress3);
                                           if(secondaryAddress3!=null){
                                             secondaryAddress3.rC_Bios__Street_Line_1__c = secondaryStreetLine1;
                                            secondaryAddress3.rC_Bios__Street_Line_2__c = secondaryStreetLine2;
                                            secondaryAddress3.rC_Bios__City__c = secondaryCity;
                                            secondaryAddress3.rC_Bios__State__c = secondaryState;
                                            secondaryAddress3.rC_Bios__Country__c = secondaryCountry;
                                            secondaryAddress3.rC_Bios__Postal_Code__c = secondaryZipCode;
                                            secondaryAddress3.rC_Bios__County__c = secondaryCounty;
                                            system.debug('secondaryStreetLine1:'+secondaryStreetLine1+'secondaryStreetLine2:'+secondaryStreetLine2);
                                            system.debug('=====>secondaryAddress3 :'+secondaryAddress3);
                                            upsert secondaryAddress3; 
                                            } */
                                 }else{
                                    //====== if duplicate secondary contact not exists=============================================================
                                        //====create new secondary contact
                                        secondaryContact = createSecondaryContact(parentContact.Id);
                                        rC_Bios__Contact_Address__c secondaryContactAddress = createContactAddress(secondaryContact, 'Home', secondaryStreetLine1, secondaryStreetLine2, secondaryCity, secondaryState, secondaryZipCode, secondaryCountry, secondaryCounty);
                                        insertContactAddressList.add(secondaryContactAddress);
                                 }
                         }
                    //======================code ends here for duplicate secondary contact ===========================
                
                } else {
                    system.debug('secondaryContact... '+secondaryContact );
                    secondaryContact = updateSecondaryContact(); 
                    update secondaryContact;  
                    // Update of rc_Bios Addresses
                    if(secondaryAddress.Id == null) {
                        rC_Bios__Contact_Address__c secondaryContactAddress = createContactAddress(secondaryContact, 'Home', secondaryStreetLine1, secondaryStreetLine2, secondaryCity, secondaryState, secondaryZipCode, secondaryCountry, secondaryCounty);
                        insertContactAddressList.add(secondaryContactAddress);
                    } else {
                        secondaryAddress.rC_Bios__Street_Line_1__c = secondaryStreetLine1;
                        secondaryAddress.rC_Bios__Street_Line_2__c = secondaryStreetLine2;
                        secondaryAddress.rC_Bios__City__c = secondaryCity;
                        secondaryAddress.rC_Bios__State__c = secondaryState;
                        secondaryAddress.rC_Bios__Country__c = secondaryCountry;
                        secondaryAddress.rC_Bios__Postal_Code__c = secondaryZipCode;
                        secondaryAddress.rC_Bios__County__c = secondaryCounty;
                        upsert secondaryAddress;  
                    }                  
                }
            }
            if(parentAddress.Id == null) {
                rC_Bios__Contact_Address__c parentContactAddress = createContactAddress(parentContact, 'Home', primaryStreetLine1, primaryStreetLine2, primaryCity, primaryState, primaryZipCode, primaryCountry, primaryCounty);
                insertContactAddressList.add(parentContactAddress);
            } else {
                parentAddress.rC_Bios__Street_Line_1__c = primaryStreetLine1;
                parentAddress.rC_Bios__Street_Line_2__c = primaryStreetLine2;
                parentAddress.rC_Bios__City__c = primaryCity;
                parentAddress.rC_Bios__State__c = primaryState;
                parentAddress.rC_Bios__Country__c = primaryCountry;
                parentAddress.rC_Bios__Postal_Code__c = primaryZipCode;
                parentAddress.rC_Bios__County__c = primaryCounty;
                upsert parentAddress;  
            } 
            if(girlAddress.Id == null) {
                rC_Bios__Contact_Address__c girlContactAddress = createContactAddress(girlContact, 'Home', streetLine1, streetLine2, city, state, zipCode, country, county);
                insertContactAddressList.add(girlContactAddress);
            } else {
                girlAddress.rC_Bios__Street_Line_1__c = streetLine1;
                girlAddress.rC_Bios__Street_Line_2__c = streetLine2;
                girlAddress.rC_Bios__City__c = city;
                girlAddress.rC_Bios__State__c = state;
                girlAddress.rC_Bios__Country__c = country;
                system.debug('zipCode... '+zipCode);
                girlAddress.rC_Bios__Postal_Code__c = zipCode;
                girlAddress.rC_Bios__County__c = county; 
                upsert girlAddress;  
            } 
           
            contactAddressIds = insertContactAddress(insertContactAddressList);   
            
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
            system.debug('membershipProduct... '+membershipProduct);
            if (!Test.isRunningTest()) { 
            if(membershipProduct != null && !membershipProduct.toUpperCase().contains('NONE'))
                newOpportunity = createMembershipOpportunity(GirlRegistrationUtilty.getOpportunityRecordTypeId(GirlRegistrationUtilty.MEMBERSHIP_RECORDTYPE), girlContact,priceBookEntry);
            }
            campaignMemberList = getCampaignMember(girlContact.Id, campaignMemberIdSet);
            //if(membershipProduct != null && !membershipProduct.toUpperCase().contains('NONE') && newOpportunity != null)
                //opportunityLineItem = createOpportunityLineItem(priceBookEntry, newOpportunity); 
                                    
            // updateOpportunityType(newOpportunity, priceBookEntry, girlContact);
            updateCampaignMembers(campaignMemberList, newOpportunity);
            
            if(booleanOpportunityGrantRequested == true && councilAccount != null)
                updateCouncilAccount(councilAccount, booleanOpportunityGrantRequested);
            
            if(girlContact != null && newOpportunity != null)
                OpportunityContactRole opportunityContactRole = createOpportunityContactRole(girlContact, newOpportunity);
            
            if(councilAccount == null) {
            List<Zip_Code__c> zip = new List<Zip_Code__c>();
            String zipCodeToMatch1 = (zipCode != null && zipCode.length() > 5) ? zipCode.substring(0, 5) + '%' : zipCode + '%';
            system.debug('zipCodeToMatch... '+zipCodeToMatch1);
            zip = [Select Council__c from Zip_Code__c where name like:zipCodeToMatch1];
            councilAccount = GirlRegistrationUtilty.getCouncilAccount(zip[0].Council__c);            
            }
            if (booleanOppMembershipOnPaper || booleanOpportunityGrantRequested) {
                if (!Test.isRunningTest()) {
                if(parentContact != null && parentContact.Id != null && councilAccount.Id != null)
                    GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Community_Girl_ThankYou' + '?GirlContactId='+girlContact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+newOpportunity.Id+'&FinancialAidRequired='+String.valueOf(booleanOpportunityGrantRequested)+'&CashOrCheck='+String.valueOf(booleanOppMembershipOnPaper), parentContact);
                }
                
/************************************************************************************************************/
if(newOpportunity!=null){
            sService_Feec= Girl_RegistrationHeaderController.councilAccount.Service_Fee__c!=null ? string.valueof(Girl_RegistrationHeaderController.councilAccount.Service_Fee__c) :null;
            sCouncil_Header_Urlc = Girl_RegistrationHeaderController.councilAccount.Council_Header_Url__c!=null?Girl_RegistrationHeaderController.councilAccount.Council_Header_Url__c:null;    
                
                
             Opportunity newopp=[select ID
            ,Adult_Contact_First_Name__c
            ,Girl_First_Name__c
            ,Council_Name__c
            ,Council_Address__c
            ,Contact__r.Name
            ,Auto_Giving__c
            ,rC_Giving__Giving_Amount__c
            ,Owner.Name
            ,Owner_Title__c
            ,Owner_Phone__c
            ,Owner_Email__c 
            from Opportunity where Id=:newOpportunity.Id
            limit 1
            ];
                            
            sAdult_Contact_First_Namec=newopp.Adult_Contact_First_Name__c!=null?newopp.Adult_Contact_First_Name__c:'';
            sGirl_First_Namec=newopp.Girl_First_Name__c!=null?newopp.Girl_First_Name__c:'';
            sCouncil_Namec=newopp.Council_Name__c!=null?newopp.Council_Name__c:'';
            sCouncil_Addressc=newopp.Council_Address__c!=null?newopp.Council_Address__c:'';
            sContactrName=newopp.Contact__r.Name!=null?newopp.Contact__r.Name:'';
            sAuto_Givingc=newopp.Auto_Giving__c!=null?newopp.Auto_Giving__c:'';
            srC_GivingGiving_Amountc=string.valueof(sService_Feec==null?newopp.rC_Giving__Giving_Amount__c:(newopp.rC_Giving__Giving_Amount__c+Decimal.ValueOf(sService_Feec)));
            sOwnerName=newopp.Owner.Name!=null?newopp.Owner.Name:'';
            sOwner_Titlec=newopp.Owner_Title__c!=null?newopp.Owner_Title__c:'';
            sOwner_Phonec=newopp.Owner_Phone__c!=null?newopp.Owner_Phone__c:'';
            sOwner_Emailc=newopp.Owner_Email__c!=null?newopp.Owner_Email__c:'';
            
            
    string logo = '<div style="padding-left:10px;height:103px;background-color:#00AE58;">';
    if(sCouncil_Header_Urlc != null) {
        logo = logo + '<img src="' + sCouncil_Header_Urlc + '" style="float:left;padding-top:5px;background-color:#00AE58;"/>';
    } else {
        logo = logo + '<img src="' + Label.DefaultCouncilLogo + '" style="float:left;padding-top:5px;background-color:#00AE58;"/>';
    }
     logo = logo + '</div>';
            string EmailG='';
            EmailG +=logo;
            
            
            EmailG +='<p>Hi ' + sAdult_Contact_First_Namec +',</p>';
            EmailG +='<p>Thank you for your interest in signing '+sGirl_First_Namec+' up for Girl Scouts.</p>';            
            EmailG +='<p>You’ve indicated that you need to pay with cash or check. If you’re writing a check, please include your confirmation number on it; for both cash and checks, please print this email and mail or bring it, along with payment, to:</p>';
            EmailG +='<p>Mail to:<br/>'+sCouncil_Namec+'<br/>'+sCouncil_Addressc+'</p>';
            EmailG +='<p>From:</p>';
            EmailG +=' <p>'+sContactrName+'<br/> Confirmation number: '+sAuto_Givingc+'<br/> Amount due: $ '+srC_GivingGiving_Amountc+'</p>';
            EmailG +='<p>Once we receive your payment, we’ll send you a confirmation email. Keep in mind that it may take a few business days for us to process your payment.</p>';
            EmailG +='<p>If you have questions, feel free to reach out. I’m here to help.</p>';
            EmailG +='<p>Have a great day!</p>';
            EmailG +='<p>'+sOwnerName+'<br/>'+sOwner_Titlec+'<br/>'+sOwner_Phonec+'<br/>'+sOwner_Emailc+'</p>';
            EmailG +='';
            EmailG +='';
                        
            system.debug('GEmail==>'+EmailG );
            
            string EmailI='';
            EmailI +=logo;
            
            
            EmailI +='<p>Hi ' + sAdult_Contact_First_Namec +',</p>';
            EmailI +='<p>Thank you for your interest in signing '+sGirl_First_Namec+' up for Girl Scouts.</p>';  
            EmailI +='<p>You indicated that you’d be paying the membership fee with cash or check, but we haven’t received anything from you yet. I just wanted to check in and see if you have questions or would like some help completing the registration process.</p>';          
            EmailI +='<p>Payment does take a few days to process once we receive it, so if you’ve already sent yours please disregard this email. Once we’ve processed your payment, we’ll be sure to send you an email to confirm and let you know how to complete the next step in the process.</p>';
            EmailI +='<p>When you’re ready, if writing a check, please include your confirmation number on it. For both cash and checks, please print this email and mail or bring it, along with payment, to:</p>';
            EmailI +='<p>Mail to:<br/>'+sCouncil_Namec+'<br/>'+sCouncil_Addressc+'</p>';
            EmailI +='<p>From:</p>';
            EmailI +=' <p>'+sContactrName+'<br/> Confirmation number: '+sAuto_Givingc+'<br/> Amount due: $ '+srC_GivingGiving_Amountc+'</p>';
            EmailI +='<p>If you have questions, reach out to me anytime.</p>';
            EmailI +='<p>Have a great day!</p>';
            EmailI +='<p>'+sOwnerName+'<br/>'+sOwner_Titlec+'<br/>'+sOwner_Phonec+'<br/>'+sOwner_Emailc+'</p>';
            
            EmailI +='';
                        
            system.debug('IEmail==>'+EmailI);
            
            string EmailH='';
            EmailH +=logo;
            EmailH +='<p>Hi ' + sAdult_Contact_First_Namec +',</p>';
            EmailH +='<p>We’re excited to get '+sGirl_First_Namec+' started with her troop/group as an official Girl Scout member!</p>';            
            EmailH +='<p>You indicated that you’ll pay the membership fee with cash or check. If you’re writing a check, please include your confirmation number on it; for both cash and checks, please print this email and mail or bring it, along with payment, to:</p>';
            EmailH +='<p>Mail to:<br/>'+sCouncil_Namec+'<br/>'+sCouncil_Addressc+'</p>';
            EmailH +='<p>From:</p>';
            EmailH +=' <p>'+sContactrName+'<br/> Confirmation number: '+sAuto_Givingc+'<br/> Amount due: $ '+srC_GivingGiving_Amountc+'</p>';
            EmailH +='<p>Payment does take a few days to process once we receive it, so if you’ve already sent yours please disregard this email. Once we’ve processed your payment, we’ll be sure to send you an email to confirm and let you know how to complete the next step in the process.</p>';
            EmailH +='<p>If you have questions or need me to talk you through the process, I’m here to help.</p>';
            EmailH +='<p>Have a great day!</p>';
            EmailH +='<p>'+sOwnerName+'<br/>'+sOwner_Titlec+'<br/>'+sOwner_Phonec+'<br/>'+sOwner_Emailc+'</p>';
            EmailH +='';
            EmailH +='';
                        
            system.debug('HEmail==>'+EmailH );
                
                
                newOpportunity.Email_G__c = EmailG ; //'<b>Hi Testing,</b><br/>Email G for customer community!';
                newOpportunity.Email_H__c = EmailH ; //'<b>Hi Testing,</b><br/>Email H for customer community!';
                newOpportunity.Email_I__c = EmailI ; //'<b>Hi Testing,</b><br/>Email I for customer community!';
                update newOpportunity;
}
/************************************************************************************************************/
            
                
                Pagereference landingPage = System.Page.Community_Girl_ThankYou;//new Pagereference('/apex/');
                if(booleanOpportunityGrantRequested != null)
                    landingPage.getParameters().put('FinancialAidRequired',String.valueOf(booleanOpportunityGrantRequested));
                if(booleanOppMembershipOnPaper != null)
                    landingPage.getParameters().put('CashOrCheck',String.valueOf(booleanOppMembershipOnPaper));
                if(girlContact != null && girlContact.Id != null)
                    landingPage.getParameters().put('GirlContactId', girlContact.Id);
                if(campaignMembers != null && campaignMembers != '')
                    landingPage.getParameters().put('CampaignMemberIds',campaignMembers);
                if(newOpportunity != null)
                    landingPage.getParameters().put('OpportunityId',newOpportunity.Id);
                if(opportunityServicefee != null)
                    landingPage.getParameters().put('OpportunityServicefeeId',opportunityServicefee.Id);
                if (councilAccount != null)
                    landingPage.getParameters().put('CouncilId', councilAccount.Id);
                
                landingPage.setRedirect(true);
                return landingPage;
            }
            
            if(girlContact != null && girlContact.Id != null && councilAccount.Id != null) {
                string paymenturl = '/Community_Girl_PaymentProcessing' + '?GirlContactId='+girlContact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+newOpportunity.Id;
                if(opportunityServicefee.Id != null)
                paymenturl = '/Community_Girl_PaymentProcessing' + '?GirlContactId='+girlContact.Id + '&CouncilId='+councilAccount.Id+'&CampaignMemberIds='+campaignMembers+'&OpportunityId='+newOpportunity.Id+'&OpportunityServicefeeId='+opportunityServicefee.Id;
                string paymenturlFinal = membershipYear + Label.community_login_URL + paymenturl;
                
                CampaignMember oldCampaignMember = [Select Id,Pending_Payment_URL__c from CampaignMember where ID = :oldCampaign]; 
                oldCampaignMember.Pending_Payment_URL__c = paymenturlFinal;
                update oldCampaignMember;
            }
            Pagereference paymentProcessingPage = System.Page.Community_Girl_PaymentProcessing;//new Pagereference('/apex/');
            paymentProcessingPage.getParameters().put('GirlContactId', girlContact.Id);
            paymentProcessingPage.getParameters().put('CampaignMemberIds',campaignMembers);

            if(councilAccount != null)
                paymentProcessingPage.getParameters().put('CouncilId', councilAccount.Id);
            if(newOpportunity.Id != null)
                paymentProcessingPage.getParameters().put('OpportunityId', newOpportunity.Id);
            if(opportunityServicefee != null)
                    paymentProcessingPage.getParameters().put('OpportunityServicefeeId',opportunityServicefee.Id);
            paymentProcessingPage.setRedirect(true);
            return paymentProcessingPage;
        } catch(System.exception pException) {
            system.debug('Exception ====:  ' + pException.getMessage());
            if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                if(counterUnableToLockRow < 4) {
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

                system.debug('addressToUpdate#######'+addressToUpdate);

                if(addressToUpdate != null && addressToUpdate.Id != null)
                    addressIdVSAddressMap.put(addressToUpdate.Id, addressToUpdate);
                    //addressToUpdateList.add(addressToUpdate);
                //addressToUpdateSet.add(addressToUpdate);
            }
        }

        addressToUpdateList = addressIdVSAddressMap.values();

        update addressToUpdateList;
    }
    
    public Opportunity createMembershipOpportunity(String recordTypeId, Contact contact,PricebookEntry priceBookEntry) {
        String campaignName = '';
        String membershipYear = string.valueOf(system.today().year());

        if(priceBookEntry != null && priceBookEntry.Product2.Name.toUpperCase().contains('LIFETIME')) {
            campaignName = LIFETIME_MEMBERSHIP;
        }
        else if(priceBookEntry != null && priceBookEntry.Product2.rC_Giving__End_Date__c != null) {
            membershipYear = string.valueOf(priceBookEntry.Product2.rC_Giving__End_Date__c.year());
            campaignName = membershipYear + ' Membership';
        }
        Opportunity opportunity;
        opportunityServicefee = new Opportunity();
        List<Opportunity> lstOpportunity = new List<Opportunity>();
        Account[] accountList = [
            Select Id
              from Account 
             where Id = :contact.AccountId 
             limit 1
        ];
        Account account = (accountList != null && accountList.size() > 0) ? accountList[0] : new Account();
        
        //Campaign[] membershipCampaignList = [Select Id, Name From Campaign where Name = :campaignName limit 1];
        //Campaign campaign = (membershipCampaignList != null && membershipCampaignList.size() > 0) ? membershipCampaignList[0] : new Campaign();
        
        Campaign campaign  = GirlRegistrationUtilty.searchCampaignFromName(campaignName);
        
        if(account != null && account.Id != null && campaign != null && campaign.Id != null) {
            opportunity = new Opportunity(
                RecordTypeId = recordTypeId, 
                AccountId = account.Id, 
                CampaignId = campaign.Id, 
                rC_Giving__Giving_Amount__c = priceBookEntry.UnitPrice,  
                StageName = 'Open', 
                Type = 'Girl Membership',
                Membership_Year__c = membershipYear,
                CloseDate = System.today(),
                Girl_First_Name__c = girlContact.FirstName
            );

        if(matchingZipCode != null && matchingZipCode.Recruiter__c != null) {
            opportunity.OwnerId = matchingZipCode.Recruiter__c;
            } else {
            opportunity.OwnerId = systemAdminUser.id;
            }
        

        lstOpportunity.add(opportunity);
        // Creating opportunity for Service fee
        Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);
        system.debug('CampaignID... ' +Girl_RegistrationHeaderController.councilAccount.Payment_Campaign__c);
        system.debug('CampaignIDMain... ' +campaign.Id);
        if(Girl_RegistrationHeaderController.councilAccount.Service_Fee__c != null) {        
        opportunityServicefee = new Opportunity(
                RecordTypeId = recordTypeId, 
                AccountId = account.Id,      
                CampaignId = Girl_RegistrationHeaderController.councilAccount.Payment_Campaign__c,  
                rC_Giving__Giving_Amount__c = Girl_RegistrationHeaderController.councilAccount.Service_Fee__c,         
                StageName = 'Open', 
                Contact__c = parentContact.id,
                Type = 'Girl Membership',
                Girl_First_Name__c = girlContact.FirstName,
                CloseDate = System.today()
            );

        if(matchingZipCode != null && matchingZipCode.Recruiter__c != null) {
            opportunityServicefee.OwnerId = matchingZipCode.Recruiter__c;
            } else {
            opportunityServicefee.OwnerId = systemAdminUser.id;
            } 
        
        lstOpportunity.add(opportunityServicefee);
        }
        system.debug('lstOpportunity... '+lstOpportunity);
        if(lstOpportunity.size()>0) {
        insert lstOpportunity;
        
        // Set permission to current user
           OpportunityShare os = new OpportunityShare(OpportunityId = opportunity.id);
           os.OpportunityId = opportunity.id; // *** ERROR - not writable ***
           os.OpportunityAccessLevel = 'Read';
           os.UserOrGroupId = UserInfo.getUserId();
           insert os;
        
        updateOpportunityType(opportunity, priceBookEntry, contact);   
        
        List<OpportunityLineItem> lstopportunityLineItem = new List<OpportunityLineItem>();
        // Lineitem for opportunity
        if(opportunity.ID !=null) {
            OpportunityLineItem opportunityLineItem = new OpportunityLineItem();
            opportunityLineItem.PricebookEntryId = priceBookEntry.Id;
            opportunityLineItem.OpportunityId = opportunity.Id;
            opportunityLineItem.Quantity = 1;
    
            if(priceBookEntry != null) {
                opportunityLineItem.UnitPrice = priceBookEntry.UnitPrice;
            }  
            lstopportunityLineItem.add(opportunityLineItem);  
        }
        if(opportunityServicefee.ID != null) {
        // Adding contactRole
        OpportunityContactRole opportunityContactRole = new OpportunityContactRole(OpportunityId = opportunityServicefee.ID, ContactId = contact.ID, Role = 'Girl', IsPrimary = true);
        Database.Saveresult oppConRoleSaveResult = Database.insert(opportunityContactRole);
        // Creating LineItem for opportunityServicefee
        List<PricebookEntry > lstPricebookEntry = [
            Select Id
                 , Name
                 , Pricebook2.Description
                 , Pricebook2.IsActive
                 , Pricebook2.Name
                 , Pricebook2.Id
                 , Pricebook2Id
                 , Product2Id
                 , UnitPrice 
              From PricebookEntry 
             where Name = 'Council Service Fee' and Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
               and IsActive = true
        ];
        if(lstPricebookEntry.size()>0) {
            OpportunityLineItem CouncilServiceFeeLineItem = new OpportunityLineItem();
            CouncilServiceFeeLineItem.PricebookEntryId = lstPricebookEntry[0].Id;
            CouncilServiceFeeLineItem.OpportunityId = opportunityServicefee.Id;
            CouncilServiceFeeLineItem.Quantity = 1;
            Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);            
            CouncilServiceFeeLineItem.UnitPrice = Girl_RegistrationHeaderController.councilAccount.Service_Fee__c;
            
            lstopportunityLineItem.add(CouncilServiceFeeLineItem);             
        } 
        } 
        if(lstopportunityLineItem.size()>0)
        insert lstopportunityLineItem;
        } 
         system.debug('lstOpportunity...after '+lstOpportunity);
        Set<id> ids = new Set<id>();
        for(Opportunity opp:lstOpportunity) {
            ids.add(opp.ID);
        }
        List<Opportunity> transactionOpp = [Select Id, OwnerId from Opportunity where rC_Giving__Parent__c in :ids and recordtype.Name='Transaction'];
        for(Opportunity trs:transactionOpp) {
            if(matchingZipCode != null && matchingZipCode.Recruiter__c != null) {
                trs.OwnerId = matchingZipCode.Recruiter__c;
                } else {
                trs.OwnerId = systemAdminUser.id;
                }
        }
        update transactionOpp;
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
        ];
        return (campaignMemberList != null && campaignMemberList.size() > 0) ? campaignMemberList : new List<CampaignMember>();
    }

    public set<String> insertContactAddress(List<rC_Bios__Contact_Address__c> insertContactAddressList) {
        set<Id> contactIdSet = new set<Id>();
        set<String> contactAddressIds = new set<String>();
        List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        
        if(insertContactAddressList != null && insertContactAddressList.size() > 0) {
            for(rC_Bios__Contact_Address__c contactAddress : insertContactAddressList)
                if(contactAddress != null && contactAddress.rC_Bios__Contact__c != null)
                    contactIdSet.add(contactAddress.rC_Bios__Contact__c);
        }
        
        if(!contactIdSet.isEmpty()) {
            contactAddressList = [
                Select Id
                     , rC_Bios__Preferred_Mailing__c
                  From rC_Bios__Contact_Address__c
                 Where rC_Bios__Contact__c IN :contactIdSet
                   and rC_Bios__Preferred_Mailing__c = true 
            ];
            system.debug('contactAddressList===>'+contactAddressList);
            if(contactAddressList != null && contactAddressList.size() > 0) {
                for(rC_Bios__Contact_Address__c contactAddressNew : contactAddressList) {
                        contactAddressNew.rC_Bios__Preferred_Mailing__c = false;
                        updateContactAddressList.add(contactAddressNew);
                }
                update updateContactAddressList;
            }
        }

        if(insertContactAddressList != null && insertContactAddressList.size() > 0) {
            List<Database.Saveresult> lstContactAddressSaveResult;
            lstContactAddressSaveResult = database.insert(insertContactAddressList);
            
            for(Database.Saveresult saveresult : lstContactAddressSaveResult) {
                if(saveresult.isSuccess())
                    contactAddressIds.add(saveresult.getId());
            }
        }
        return contactAddressIds;
    }
    
    public rC_Bios__Contact_Address__c createContactAddress(Contact contact, String addrType, String addrStreetLine1, String addrStreetLine2, String addrCity, 
                    String addrState, String addrZipCode, String addrCountry, String addrCounty) {
        rC_Bios__Contact_Address__c contactAddress;
        List<rC_Bios__Contact_Address__c> contactAddressList = new List<rC_Bios__Contact_Address__c>();
        List<rC_Bios__Contact_Address__c> updateContactAddressList = new List<rC_Bios__Contact_Address__c>();
        
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
            contactAddress.rC_Bios__Original_State__c = addrState != null ? addrState.substring(0, 2) : '';
            contactAddress.rC_Bios__Original_Postal_Code__c = addrZipCode != null ? addrZipCode : '';
            contactAddress.rC_Bios__Original_Country__c = addrCountry != null ? addrCountry : '';
            contactAddress.rC_Bios__Preferred_Mailing__c = true;
        }
        return contactAddress;
    }
    /***
    public Zip_Code__c getZipCode(String zipCode) {
        Zip_Code__c[] zipCodeList = [
            Select Id
                 , Name
                 , Council__c
                 , Zip_Code_Unique__c
                 , City__c
                 , Recruiter__c 
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

        if(priceBookEntry != null) {
            opportunityLineItem.UnitPrice = priceBookEntry.UnitPrice;
            totalPrice = totalPrice + priceBookEntry.UnitPrice;
        }
        //insert opportunityLineItem;
        // Council Service Fee OpportunityLineItem is not creating now!
        List<PricebookEntry > lstPricebookEntry = [
            Select Id
                 , Name
                 , Pricebook2.Description
                 , Pricebook2.IsActive
                 , Pricebook2.Name
                 , Pricebook2.Id
                 , Pricebook2Id
                 , Product2Id
                 , UnitPrice 
              From PricebookEntry 
             where Name = 'Council Service Fee' and Pricebook2.Name = 'Girl Scouts USA'
               and Pricebook2.IsActive = true
        ];
        if(lstPricebookEntry.size()>0) {
            OpportunityLineItem CouncilServiceFeeLineItem = new OpportunityLineItem();
            CouncilServiceFeeLineItem.PricebookEntryId = lstPricebookEntry[0].Id;
            CouncilServiceFeeLineItem.OpportunityId = newOpportunity.Id;
            CouncilServiceFeeLineItem.Quantity = 1;
            Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);
            if(Girl_RegistrationHeaderController.councilAccount.Service_Fee__c != null) {
            CouncilServiceFeeLineItem.UnitPrice = Girl_RegistrationHeaderController.councilAccount.Service_Fee__c;
            totalPrice = totalPrice + Girl_RegistrationHeaderController.councilAccount.Service_Fee__c;
            }
            insert CouncilServiceFeeLineItem;
        }   
        /// 
        return opportunityLineItem;
    }
    ***/
    public void updateOpportunityType(Opportunity newOpportunity, PricebookEntry priceBookEntry, Contact contact) {
        
        if(newOpportunity != null && priceBookEntry != null && contact != null) {
            newOpportunity.Type = (priceBookEntry.Name.toUpperCase().contains('GIRL')) ? 'Girl Membership' 
                                    :((priceBookEntry.Name.toUpperCase().contains('GIRL')) ? 'Girl Membership' : newOpportunity.Type);  

            newOpportunity.Contact__c = contact.Account.rC_Bios__Preferred_Contact__c;
            newOpportunity.Membership_Status__c = (contact.Ineligible__c == true) ? 'Ineligible' : 'Payment Pending';
            newOpportunity.rC_Giving__Primary_Contact__c = contact.Id;
            newOpportunity.Adult_Email__c = contact.Account.rC_Bios__Preferred_Contact__r.email;             
            //newOpportunity.rC_Giving__Giving_Amount__c = totalPrice;
            if(booleanOppMembershipOnPaper)
                newOpportunity.Membership_on_Paper__c = true;
            if(booleanOpportunityGrantRequested)
                newOpportunity.Grant_Requested__c = true;
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
}