<apex:page showheader="true" sidebar="false" controller="Community_GirlPaymentProcessing" standardStyleSheets="false" cache="false">
    <!-- <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <apex:includeScript value="{!URLFOR($Resource.JQuery, '/jquery/jquery-1.9.1.min.js')}" />
    <apex:includeScript value="{!URLFOR($Resource.JQuery, '/jquery/jquery-ui.js')}" />
    
    <apex:stylesheet value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/css/bootstrap.css')}" />
    <apex:stylesheet value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/css/bootstrap-theme.css')}" />
    <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/bootstrap.js')}" />
     
    <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/moment.js')}" />
    <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/bootstrap-datetimepicker.js')}" />
    <apex:stylesheet value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/css/bootstrap-datetimepicker.css')}" />
     -->
      <!-- <style>
/*              table,th,td */
/*              { */
/*                  border:1px solid black; */
/*                  border-collapse:collapse; */
/*              } */
/*              th,td */
/*              { */
/*                  padding:5px; */
/*              } */
             .form-control {
             background-image: none;!important}
             .select-list{
        -webkit-appearance: none;
        border-radius:3px;
        background-image:url("{!$Resource.DownArrow}");
        background-repeat : no-repeat;
        background-position: 98% 10px;
        -webkit-appearance: none;
        -moz-appearance: none;
         text-indent: 0.01px;
        text-overflow: "";
        appearence:none;
        
        
    }
        select::-ms-expand {
    display: none;
    }

    .messageCell   { 
        background-color: #ffc !important;
     } 
     .container{ 
         margin-top: 10px; 
     } 
    .message {  
      background-color: #ffc !important;  
      border-style: solid !important; 
      border-width: 1px !important; 
      color: #000 !important; 
      padding: 6px 8px 6px 6px !important; 
      margin: 4px 20px !important; 
      }  
      .warningM3 { 
          border-color: #f90 !important;  
     }  
       </style>-->
       <script type="text/javascript" language="javascript">
function DisableBackButton() {
window.history.forward()
}
DisableBackButton();
window.onload = DisableBackButton;
window.onpageshow = function(evt) { if (evt.persisted) DisableBackButton() }
window.onunload = function() { void (0) }
</script>

       <Style>
       button_reg{
        font-family:Open Sans,Arial!important;
        font-size: 16px!important;
        font-weight:bold!important;
        padding: 6px 10px 6px 8px!important;
        height: 35px!important;
        color: #fff!important;
        text-decoration: none!important;
        text-align: center!important;
        background: #00AE58!important;
        width:100%!important;
        }
       </style>
       <style>
       .req{font-size: 140%;color: red;padding-left:3px;}
       </style> 
        <apex:includeScript value="{!URLFOR($Resource.JQuery, '/jquery/jquery-1.9.1.min.js')}" />
        <apex:includeScript value="https://code.jquery.com/ui/1.10.3/jquery-ui.js" />
            <apex:stylesheet value="https://code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css" />
          
            <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/bootstrap.js')}" />
            <apex:includeScript value="{!URLFOR($Resource.Bootstrap, '/Bootstrap/js/bootstrap.min.js')}" />
        <script src="//d79i1fxsrar4t.cloudfront.net/jquery.liveaddress/2.4/jquery.liveaddress.min.js"></script>
        <script>
            jQuery(function() {
                $.LiveAddress({
                    key: '5087890286109288209',
                    debug: false,
                    submitVerify: false
                });
            });
         </script>
        <apex:stylesheet value="{!$Resource.CommunityScreenCss}" />
        <apex:outputPanel styleClass="isSiteDownPanel" html-isSiteDown="{!$Setup.Site_Maintenance_Setting__c.Girl_Renewal_Maintenance__c}" />
    <script>
    var cachebuster = Math.round(new Date().getTime() / 1000);
        $("body").hide();
        
        if ( $(".isSiteDownPanel").attr("isSiteDown") == "true") { 
              var baseUrl = "{!$Site.CurrentSiteUrl}";
              window.location.href = baseUrl + 'InMaintenance';
        }
        $("body").show();
    </script>
    <apex:form styleClass="form-horizontal">
       
        
     <div class="container">
     <apex:pageMessages id="errorMessage"/>
            
<!--         </div> -->
               
        <div class="row">
                    <lable style="font-size:22px;font-weight:bold;font-family: 'Open Sans','Arial',sans-bold !important;">Pay for your membership.</lable>
                    <apex:outputLabel value="At Girl Scouts, we believe every girl should have a chance to shine. But some girls need help to cover costs like membership and uniforms in order to participate. A small contribution can help us provide scholarships that directly benefit girls in our area—so more girls can join the fun and unlock their potential with Girl Scouts." id="fiL1abel" styleClass="col-sm-12   myFirstName"  style="text-align: left;font-family: 'Open Sans','Arial',sans-bold !important;"/><br/>
<!--                     <apex:outputLabel value="YES! I would like to make a donation today that directly benefits girls in our area." id="fiLabel2" styleClass="col-sm-12   myFirstName"  style="text-align: left;"/>-->
                </div>
                
                  <br/>
            <div class="row">
                    <apex:outputLabel value="Here's what your tax-deductible donation can do:"  id="fiLabel11" styleClass="col-sm-10 " style="text-align: left;font-family: 'Open Sans','Arial',sans-bold !important;"/>
                </div>
                
                <div class="row">
                    <apex:outputLabel value="$15 helps with her membership fee." id="fiLabel12" styleClass="col-sm-10 "  style="text-align: left; margin-left: 60px;font-family: 'Open Sans','Arial',sans-bold !important;"/>
                </div>
                    
                <div class="row">
                    <apex:outputLabel value="$50 helps her buy a uniform." id="fiLabel13" styleClass="col-sm-10 "  style="text-align: left; margin-left: 60px;font-family: 'Open Sans','Arial',sans-bold !important;"/>
                </div>
                
                <div class="row">
                    <apex:outputLabel value="$150 helps with membership fees for a troop of 10." id="fiLabel14" styleClass="col-sm-10 "  style="text-align: left; margin-left: 60px;font-family: 'Open Sans','Arial',sans-bold !important;"/>
                </div>
              <div class="form-group">
           <br/>
           <apex:outputPanel layout="none" rendered="{!NOT(confirmTransactions)}">
             <apex:outputLabel value="I want to donate:" id="donationLabel1" styleClass="col-sm-2"  style="padding-left:0px; text-align:left;font-family: 'Open Sans','Arial',sans-bold !important;"/>
            <table class="table-responsive"  width="100%">
                <tr style="margin-top:5px">
                    <td  style="background-color:#fff !important; width: 8%;"> 
                        <input type="radio" name="optionsRadios" id="DonationRadios1" value="{!option1Value}" onclick="onPaymentCheked(event,this,this.value)"/>
                        <apex:outputLabel value="${!option1Value}" id="donationFillLabel1" style="margin-left:10px;font-family: 'Open Sans','Arial',sans-bold !important; "  />
                    </td>
                    <td style="background-color:#fff !important; width: 8%;">
                        <input type="radio" name="optionsRadios" id="DonationRadios2" value="{!option2Value}" onclick="onPaymentCheked(event,this,this.value)"/>
                        <apex:outputLabel value="${!option2Value}" id="donationFillLabel2" style="margin-left:10px;font-family: 'Open Sans','Arial',sans-bold !important; "  />
                    </td>
                    <td style="background-color:#fff !important; width: 8%;">
                        <input type="radio" name="optionsRadios" id="DonationRadios3" value="{!option3Value}" onclick="onPaymentCheked(event,this,this.value)"/>
                        <apex:outputLabel value="${!option3Value}" id="donationFillLabel3" style="margin-left:6px;font-family: 'Open Sans','Arial',sans-bold !important; "  />
                    </td>
                    <td style="background-color:#fff !important; width: 8%;"> 
                        <input type="radio" name="optionsRadios" id="DonationRadios4" value="{!option4Value}" onclick="onPaymentCheked(event,this,this.value)"/>
                        <apex:outputLabel value="${!option4Value}" id="donationFillLabel4" style="margin-left:6px; font-family: 'Open Sans','Arial',sans-bold !important;"  />
                    </td>
                    <td style="background-color:#fff !important; width: 8%;">
                        <input type="radio" name="optionsRadios" id="DonationRadios5" value="{!option5Value}" onclick="onPaymentCheked(event,this,this.value)"/>
                        <apex:outputLabel value="${!option5Value}" id="donationFillLabel5" style="margin-left:6px;font-family: 'Open Sans','Arial',sans-bold !important; "  />
                    </td>
                    <td style="background-color:#fff !important;">
                        <input type="radio" name="optionsRadios" id="DonationRadios6" value="{!option6Value}" onclick="onPaymentCheked(event,this,this.value)"/>
                        <apex:outputLabel value="${!option6Value}" id="donationFillLabel6" style="margin-left:10px;font-family: 'Open Sans','Arial',sans-bold !important; "   />
                    </td>
                </tr>
                 <tr><td colspan="6">
                <table class="table-responsive"  width="100%">
                    <tr style="margin-top:5px">
                        <td style="background-color:#fff !important; width: 32%;">
                            <div style="float:left;">
                               <input type="radio" name="optionsRadios" id="optionsRadios4" value="{!otherPayment}" onclick="getOtherTextValue(event,this);" class="otherRadio"/>
                               <apex:outputLabel value="Other" id="fiL3abel" style="margin-left:10px;margin-right:5px;font-family: 'Open Sans','Arial',sans-bold !important;"  /></div>
                               <div  style="float:left;width:30%">
                                   <input type = "text" id="otherPaymentInputText" class="form-control otherPayment otherCheckBox" value="{!otherPayment}" onChange="getOtherTextValue(event,this);" disabled="disabled"/>
                               </div>
                               <div  style="float:left;margin-left:5px;font-family: 'Open Sans','Arial',sans-bold !important;">
                                   <apex:outputLabel value="(Minimum $10.00)" id="fiL33Label"   />
                               </div>
                        </td>
                        <td style="background-color:#fff !important;">
                            <input type="radio" name="optionsRadios" id="optionsRadios5" value="{!NotAtThisTime}" checked = "checked" onclick="onPaymentCheked(event,this,this.value)"/>
                            <apex:outputLabel value="Not at this Time" id="fiL37abel"  style="margin-left:10px; font-family: 'Open Sans','Arial',sans-bold !important;"/>
                        </td>
                        <td style="background-color:#fff !important;"></td>
                    </tr>
                </table>
                </td>
                </tr>
            </table>
            </apex:outputPanel>
        </div>
        
        <table class="table table-striped">
        <tr><td>
        <apex:actionFunction name="pymentCheckedAction" action="{!createTransactionRecord}" reRender="resultPanel" status="myStatus">
            <apex:param name="arg1" value="val" assignTo="{!amountValue}"/>
       
        </apex:actionFunction>
        <input type="hidden" name="donationAmount" id="donationAmount" value="{!amountValue}"/>
  </td></tr>
</table>
       
     
<!-- c------------------------------------------------------------------------------------------------------------------------c -->

       <apex:outputPanel id="resultPanel" >

            <table class="table  table-bordered table-striped">
            <tr>
               <td style = "font-weight:bold; text-align: right;font-family: 'Open Sans','Arial'!important;">Item</td>
               <td style = "font-weight:bold; text-align: right;font-family: 'Open Sans','Arial'!important;">Total</td>
            </tr>
       <apex:repeat value="{!donationMap}" var="key" id="theRepeat">
       <tr>
            <td style="text-align: right;">
                <apex:outputText value="{!key}" />
            </td>
            <td style="text-align: right;">
                <span>$</span><apex:outputText value="{!donationMap[key]}" style="text-align: right;"/>
            </td>
       </tr>
       </apex:repeat>
        <tr>
            <td style="text-align: right;">
                <apex:outputLabel value="Total" id="totalLabel"  styleClass="myFirstName" style="text-align: right;font-family: 'Open Sans','Arial',sans-bold !important;"/>
            </td>
            <td style = "font-weight:bold; text-align: right;">
                <span>$</span><apex:outputText value="{!total}" style="text-align: right;"/>
            </td>
       </tr>
       </table>
     </apex:outputPanel>
       
          
       
<!-- c------------------------------------------------------------------------------------------------------------------------c -->
    <apex:outputPanel layout="block" rendered="{!AND(confirmTransactions)}" style="margin-bottom: 2em;">
        <div class="col-sm-3" style="margin-bottom: 30px!important;">
        <apex:commandButton value="Back" id="test2" action="{!processPaypalTransactionsUndo}" styleClass="btn btnback button_reg"/>
        </div>
        <div class="col-sm-3" style="margin-bottom: 30px!important;">
        <apex:commandButton value="Process Charges" id="test" action="{!processPaypalTransactions}" styleClass="btn button_reg processChange"/>
        <apex:commandButton value="Processing..." id="testdisabled" disabled="true" styleClass="btn button_reg processNone" style="display: none;" />
        </div>
     
    </apex:outputPanel>
    
    <apex:outputPanel layout="none" rendered="{!NOT(confirmTransactions)}">
        
        <div style="margin: 0px auto;" class="col-sm-12">
        
        
        <div class="form-group" style="margin-top: 2%;">
            <h4> <strong> 
            <div class="col-sm-3"></div>
            <apex:outputLabel value="Billing Address" id="billingLabel" styleClass="col-sm-2 myFirstName"  style="text-align:right;padding-right:30px;font-family: 'Open Sans','Arial',sans-bold !important;"/> </strong></h4>
            <div class="col-sm-3">
                    <apex:outputLink id="commandTxt" value="" onclick="fillMailingAddress(); return false;"> <strong> <U> Same as my Mailing Address </U> </strong> </apex:outputLink> 
<!--                     <apex:commandLink id="commandTxt"  value="Same as my Mailing Address"  />  -->
            </div>
        </div>
        
        
        
 <!--       <div class="form-group" style="margin-top: 2%;">
        <div class="col-sm-3"></div>
 <!--            <apex:outputLabel value="First Name:" id="firstNameLabel" styleClass="col-sm-2   myFirstName" />
            <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;">*</div> -->
            <!--<div class="col-sm-3">
                    <apex:inputText id="firstNameInputTxt" styleClass="form-control firstName" value="{!firstName}"/> 
             <span class="req">*</span>
            </div>-->
            <!--<div style="font-size: 180%;color: red;">*</div>
        </div>-->
        
        <div class="form-group">
        <div class="col-sm-3"></div>
 <!--           <apex:outputLabel value="Last Name:" id="lastNameLabel" styleClass="col-sm-2  " />
             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;">*</div> -->
            <!--<div class="col-sm-3">
                <apex:inputText id="lastNameInputTxt"  styleClass="form-control lastName" value="{!lastName}"/>
             <span class="req">*</span>
            </div>-->
            <!--<div style="font-size: 180%;color: red;">*</div>-->
        </div>
        <apex:outputPanel id="paymentAddressPnl">
        
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="Address:" id="addressLabel" styleClass="col-sm-2  " style="text-align:right;padding-right:30px;font-family: 'Open Sans','Arial',sans-bold !important;"/>
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;"></div> -->
            <div class="col-sm-3">
                <apex:inputText id="addressTxt"  styleClass="form-control myAddress" value="{!address}"/>
            </div>
             <div style="font-size: 180%;color: red;">*</div> 
        </div>
        
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="City:" id="cityLabel" styleClass="col-sm-2  " style="text-align:right;padding-right:30px;font-family: 'Open Sans','Arial',sans-bold !important;"/>
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;"></div> -->
            <div class="col-sm-3">
                <apex:inputText id="cityTxt"  styleClass="form-control myCity" value="{!city}"/>
            </div>
            <div style="font-size: 180%;color: red;">*</div>
        </div>
        
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="State:" id="stateLabel" styleClass="col-sm-2" style="text-align:right;padding-right:30px;font-family: 'Open Sans','Arial',sans-bold !important;" />
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;"></div> -->
            <div class="col-sm-3">
               <apex:inputText value="{!state}"  id="stateTxt" styleClass="form-control stateClass"/>
              <!--<span class="req">*</span>-->
            </div>
           <div style="font-size: 180%;color: red;">*</div>
        </div>
        
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="Zip Code:" id="zipLabel" styleClass="col-sm-2" style="text-align:right;padding-right:30px; font-family: 'Open Sans','Arial',sans-bold !important;" />
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;"></div> -->
            <div class="col-sm-3">
                <apex:inputText id="zipcode"  styleClass="form-control myZipCode" value="{!zipCode}"/> <!-- if you want small sizeer textbos add "input-sm" in styleClass -->
            </div>
         <div style="font-size: 180%;color: red;margin-left:15px;float:left;">*</div>
        </div>
        </apex:outputPanel>
       
        
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="Country:" id="countryLabel" styleClass="col-sm-2" style="text-align:right;padding-right:30px;font-family: 'Open Sans','Arial',sans-bold !important;" />
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;">*</div> -->
            <div class="col-sm-3">
                <apex:selectList value="{!country}" multiselect="false" size="1" styleClass="form-control listCountryItemsClass select-list ">
                     <apex:selectOptions value="{!listCountryItems}"/>
                 </apex:selectList>
              <!-- <span class="req">*</span>-->
            </div>
            <div style="font-size: 180%;color: red;">*</div>
            </div>
            
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="Card Holder Name:" id="cardNameLabel" styleClass="col-sm-2" style="text-align:right;padding-right:30px; font-family: 'Open Sans','Arial',sans-bold !important;" />
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;">*</div> -->
            <div class="col-sm-3">
                <apex:inputText id="cardNameTxt"  styleClass="form-control myCardHolderName" value="{!cardHolderName}"/>
           <!--<span class="req">*</span>-->
            </div>
            <div style="font-size: 180%;color: red;">*</div>
        </div>
        
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="Card Number:" id="cardNumberLabel" styleClass="col-sm-2" style="text-align:right;padding-right:30px; font-family: 'Open Sans','Arial',sans-bold !important;" />
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;">*</div> -->
            <div class="col-sm-3">
                <apex:inputText id="cardNumberTxt"  styleClass="form-control myCardNumber" value="{!cardNumber}"/>
             <!--<span class="req">*</span>-->
            </div>
            <div style="font-size: 180%;color: red;">*</div>
        </div>
        
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="Expiration Month:" id="expirationLabel" styleClass="col-sm-2" style="text-align:right;padding-right:30px; font-family: 'Open Sans','Arial',sans-bold !important;"/>
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;">*</div> -->
                <div class= "col-sm-3">
                    <apex:selectList value="{!expMonth}" multiselect="false" size="1" styleClass="form-control listexpMonthClass select-list">
                     <apex:selectOptions value="{!listexpMonth}"/> 
                 </apex:selectList>
                 <!--<span class="req">*</span>-->
                 </div>
                 <div style="font-size: 180%;color: red;">*</div>
              </div>
              
              <div class="form-group">
              <div class="col-sm-3"></div>
                  <apex:outputLabel value="Expiration Year:" id="expirationLabel1" styleClass="col-sm-2" style="text-align:right;padding-right:30px; font-family: 'Open Sans','Arial',sans-bold !important;"/>
    <!--               <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;"></div> -->
                <div class="col-sm-3">
                    <div  id='datetimepicker5' data-date-format="YYYY/MM/DD">
                        <apex:selectList value="{!expYear}" multiselect="false" size="1" styleClass="form-control listexpYearClass select-list">
                             <apex:selectOptions value="{!listexpYear}"/>  
                         </apex:selectList>
                    <!--<span class="req">*</span>-->
                    </div>
                
               </div>
               <div style="font-size: 180%;color: red;">*</div>
            </div>
        
        <div class="form-group">
        <div class="col-sm-3"></div>
            <apex:outputLabel value="Security Code:" id="securityLabel" styleClass="col-sm-2" style="text-align:right;padding-right:30px; font-family: 'Open Sans','Arial',sans-bold !important;"/>
<!--             <div class="col-sm-1" style="font-size: 180%;color: red; text-align:left;">*</div> -->
            <div class="col-sm-3">
                <apex:inputText id="securityTxt"  styleClass="form-control mySecurityCode" value="{!securityCode}"/>
             <!--<span class="req">*</span>-->
            </div>
            <div style="font-size: 180%;color: red;">*</div>
        </div>
        
        </div>
            <div class="form-group">
<!--         <apex:inputCheckbox styleClass="col-sm-4  " /> -->
               
<!--               <div class="col-sm-5"> -->
<!--                <div class="checkbox"> -->
<!--                    <label> -->
<!--                     <input type="checkbox" value="{!acceptGSPromiseAndLaw}" id="inputCheck" styleClass="inputCheckCls"/> -->
<!--                        <apex:inputCheckbox value="{!acceptGSPromiseAndLaw}" id="inputCheck" styleClass="inputCheckCls" /> -->
<!--                        <apex:outputLabel >I/We accept and abide by the Girl Scout Promise and Law</apex:outputLabel> -->
<!--                    </label> -->
<!--                </div> -->
<!--            </div> -->
                 <div class="form-group">
            <div class="col-sm-5"></div>
            
            <div class="col-sm-3">
               
               <apex:commandButton value="Confirm" id="nextBtn" action="{!processMyOrder}" styleClass="button_reg submitButton" style="width:275px!important;margin-left: 11px;" status="buttons_group_status"></apex:commandButton>
                <input class="button_reg submitButtonprocessing2" disabled="disabled" style="width:275px!important;margin-left: 11px;display: none;" value="Processing..." ></input>

<!--                <apex:commandButton value="Process My Order" id="nextBtn"  action="{!processMyOrder}" onclick="conformationPopup();" styleClass="button_reg submitButton" style="width:275px!important;margin-left: 11px;" /> -->
             
            </div>
            
        </div>
               
                <!--<div class="col-sm-12 butright">
             <apex:commandButton value="Process My Order" id="nextBtn"  action="{!processMyOrder}" onclick="conformationPopup();" styleClass="btn btn-default submitButton" />
          </div>-->
      </div>
      </apex:outputPanel>
      </div>
       <apex:actionfunction name="fillMailingAddress" action="{!fillPaymentMailingAddress}" rerender="paymentAddressPnl" />
        <!--<apex:outputLink id="nextBtn" styleClass="btn btn-default submitBtnClk" style="margin-left: 80%;">Next</apex:outputLink>-->        
    </apex:form>
       <div class="modal fade" id="mybtnPopup" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
            <div class="modal-dialog_ksadjfhdkjh" >
            <div style="top:50%; left:50%; position:absolute;">
            <img  class="waitingImage" src="/img/loading.gif" title="Please Wait..." /><span style="color:#ffffff;" class="waitingDescription">Loading...</span>
            </div>
            </div>
            </div> 
<script type="text/javascript">
 $(".submitButton").show();
        $(".submitButtonprocessing2").hide();
        $(".processChange").show();
        $(".btnback").show();
        $(".processNone").hide();

        $('.processChange').click(function(){
            console.log('Submit button clicked...');
            $(".processChange").hide();
            $(".processNone").show();
            $(".btnback").hide();
              $('#mybtnPopup').modal('toggle');
        });
   
   
   
        var selectedAmount = $("#donationAmount").val();
            selectedAmount =  Math.round(selectedAmount);
       
        if(selectedAmount == 15){
         $('#DonationRadios1').prop('checked', true);
         $('#optionsRadios5').prop('checked', false);
        }
        if(selectedAmount == 50){
         $('#DonationRadios2').prop('checked', true);
         $('#optionsRadios5').prop('checked', false);
        }
        if(selectedAmount == 100){
         $('#DonationRadios3').prop('checked', true);
         $('#optionsRadios5').prop('checked', false);
        }
        if(selectedAmount == 150){
         $('#DonationRadios4').prop('checked', true);
         $('#optionsRadios5').prop('checked', false);
        }
        if(selectedAmount == 250){
         $('#DonationRadios5').prop('checked', true);
         $('#optionsRadios5').prop('checked', false);
        }
        if(selectedAmount == 500){
         $('#DonationRadios6').prop('checked', true);
         $('#optionsRadios5').prop('checked', false);
        }
        if(selectedAmount != 15 && selectedAmount != 50 && selectedAmount != 100 && selectedAmount != 150 && selectedAmount != 250 && selectedAmount != 500 && selectedAmount !=0){
          $('#optionsRadios4').prop('checked', true);
          $('#optionsRadios5').prop('checked', false);
          $('#otherPaymentInputText').val(selectedAmount);
        }
 
        
            
            
            
                $(".otherCheckBox").keypress(function (e) {
                    $(this).siblings("div").remove();
                    //if the letter is not digit then display error and dont type anything
                    if (e.which != 8 && e.which != 0 && (e.which < 48 || e.which > 57)) {
                          //display error message
                         $(this).after("<div>Digits Only</div>");
                         return false;
                    }
                });
                
                $(".listexpMonthClass").on('blur',function(){
                    var listexpMonthVar = $(this).val();
                    if(listexpMonthVar === '--None--' || listexpMonthVar === '--None--') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> Month is Required.</div>");
                        $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                         $(this).removeClass(".alert alert-danger");
                    }
                });
                
                $(".listexpYearClass").on('blur',function(){
                    var listexpYearVar = $(this).val();
                    if(listexpYearVar === '--None--' || listexpYearVar === '--None--') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> Year is Required.</div>");
                        $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                         $(this).removeClass(".alert alert-danger");
                    }
                });
                
                $(".listCountryItemsClass").on('blur',function(){
                    var listCountryItemsVar = $(this).val();
                    if(listCountryItemsVar === '--None--' || listCountryItemsVar === '--None--') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> Country is Required.</div>");
                        $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                         $(this).removeClass(".alert alert-danger");
                    }
                });
            /*
                $(".firstName").on('blur',function(){
                    console.log('validating first name....');
                    var firstName = $(this).val();
                    if(firstName === null || firstName === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> First Name is Required.</div>");
                          $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");
                    }
                });
            
                $(".lastName").on('blur',function(){
                    console.log('validating last name....');
                    var lastName = $(this).val();
                    if(lastName === null || lastName === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> Last Name is Required.</div>");
                          $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");
                    }
                });
            */    
                $(".myCardHolderName").on('blur',function(){
                    console.log('validating streetLine1....');
                    var carHolderName = $(this).val();
                    if(carHolderName === null || carHolderName === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div>Card Holder Name is Required.</div>");
                        $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");
                    }
                });
                
               $(".myCardNumber").on('blur',function(){
                    console.log('validating streetLine2....');
                    var cardNumber = $(this).val();
                        cardNumber = $.trim(cardNumber);
                    var cardType = getCardType(cardNumber);

                    if(cardNumber === null || cardNumber === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div>Card Number is Required.</div>");
                        $(this).addClass(".alert alert-danger");
                    } else if(cardNumber.match(/^[0-9-+]+$/) === null){
                        $(this).siblings("div").remove();
                        $(this).after("<div> Invalid Card Number .</div>");
                        $(this).addClass(".alert alert-danger");
                    }else if(cardNumber.length > 16) {
                        $(this).siblings("div").remove();
                        $(this).after("<div>Card Number should be smaller than 16 digits.</div>");
                        $(this).addClass(".alert alert-danger");
                    }else if(cardType == "unknown") {
                        $(this).siblings("div").remove();
                        $(this).after("<div>Invalid card type.</div>");
                        $(this).addClass(".alert alert-danger");
                    }else {
                        $(this).siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");

                        var securityCode = $(".mySecurityCode").val();
                        if(securityCode.length > 0){

                            $(this).siblings("div").remove();
                            $(this).removeClass(".alert alert-danger");

                            $(".mySecurityCode").siblings("div").remove();
                            $(".mySecurityCode").removeClass(".alert alert-danger");

                            if(cardType == "amex" && securityCode.length != 4) {
                                $(".mySecurityCode").after("<div>Security Code must be 4 digits.</div>");
                                $(".mySecurityCode").addClass(".alert alert-danger");
                            } else if(cardType != "amex" && securityCode.length != 3) {
                                $(".mySecurityCode").after("<div>Security Code must be 3 digits.</div>");
                                $(".mySecurityCode").addClass(".alert alert-danger");
                            }
                        }
                    }
                });
                $(".expirationCode").on('blur',function(){
                    console.log('validating city....');
                    var expirationCode = $(this).val();
                    if(expirationCode === null || expirationCode === '') {
                        $(this).siblings("div").remove();
                        $(".input-group").siblings("div").remove();
                        $(".input-group").after("<div> Expiry Code is  Required.</div>");
                          $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                        $(".input-group").siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");
                    }
                });
              $(".myZipCode").on('blur',function(){
                    console.log('validating zipCode....');
                    var zipCode = $(this).val();
                    if(zipCode === null || zipCode === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> Zip Code is Required.</div>");
                          $(this).addClass(".alert alert-danger");
                    } else if(zipCode.match(/^[0-9-+]+$/) === null){
                        $(this).siblings("div").remove();
                        $(this).after("<div> Invalid Zip Code.</div>");
                          $(this).addClass(".alert alert-danger");
                    }
                    else {
                        $(this).siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");
                    }
                });
                
                    $(".myAddress").on('blur',function(){
                    var address = $(this).val();
                    if(address === null || address === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> Address is Required.</div>");
                        $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                         $(this).removeClass(".alert alert-danger");
                    }
               });
               
               $(".myCity").on('blur',function(){
                    var city = $(this).val();
                    if(city === null || city === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> City is Required.</div>");
                        $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                         $(this).removeClass(".alert alert-danger");
                    }
               });
                $(".stateClass").on('blur',function(){
                   var state = $(this).val();
                    if(state === null || state === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div> State is Required.</div>");
                        $(this).addClass(".alert alert-danger");
                    } else {
                        
                    
                         $(this).siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");
                     
                    }
               });
                $(".mySecurityCode").on('blur',function(){
                    console.log('validating homePhone....');
                    var security = $(this).val();
                    if(security === null || security === '') {
                        $(this).siblings("div").remove();
                        $(this).after("<div>Security Code is Required.</div>");
                          $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");

                        var creditCardNo = $(".myCardNumber").val();
                        if (creditCardNo.length == 0) {
                            $(".myCardNumber").siblings("div").remove();
                            $(".myCardNumber").removeClass(".alert alert-danger");

                            $(".myCardNumber").after("<div>Please enter card number.</div>");
                            $(".myCardNumber").addClass(".alert alert-danger");
                        } else {
                            $(".myCardNumber").siblings("div").remove();
                            $(".myCardNumber").removeClass(".alert alert-danger");

                            var cardType = getCardType(creditCardNo);
                            if(cardType == "unknown") {
                                $(".myCardNumber").after("<div>Invalid card type.</div>");
                                $(".myCardNumber").addClass(".alert alert-danger");
                            } else if(cardType == "amex" && security.length != 4) {
                                $(this).after("<div>Security Code must be 4 digits.</div>");
                                $(this).addClass(".alert alert-danger");
                            } else if(cardType != "amex" && security.length != 3) {
                                $(this).after("<div>Security Code must be 3 digits.</div>");
                                $(this).addClass(".alert alert-danger");
                            }
                        }
                    }
                });

                $(".myEmail").on('blur',function(){
                    console.log('validating dateOfBirth....');
                    var email = $(this).val();
                    if(email === null || email === '') {
                        $(this).siblings("div").remove();
                       $(".input-group").siblings("div").remove();
                        $(".input-group").after("<div>Email is Required.</div>");
                          $(this).addClass(".alert alert-danger");
                    }else {
                        $(this).siblings("div").remove(); 
                         $(".input-group").siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");
                    }
                });
                $(".myAmount").on('blur',function(){
                    console.log('validating dateOfBirthChange....');
                    var amount = $(this).val();
                    if(amount === null || amount === '') {
                        $(this).siblings("div").remove();
                       $(".input-group").siblings("div").remove();
                        $(".input-group").after("<div> Amount is Required.</div>");
                          $(this).addClass(".alert alert-danger");
                    } else {
                        $(this).siblings("div").remove();
                         $(".input-group").siblings("div").remove();
                        $(this).removeClass(".alert alert-danger");
                    }
                });

            function getCardType(cardNumber){
                var result = 'unknown';
                if(new RegExp("^5[1-5][0-9]{14}$", "i").test(cardNumber)) {
                    result = 'mastercard';
                }else if(new RegExp("^4[0-9]{12}(?:[0-9]{3})?$", "i").test(cardNumber)) {
                    result = 'visa';
                }else if(new RegExp("^3[47][0-9]{13}$", "i").test(cardNumber)) {
                    result = 'amex';
                }else if(new RegExp("^6(?:011|5[0-9]{2})[0-9]{12}$", "i").test(cardNumber)) {
                    result = 'discover';
                }else if(new RegExp("^3(?:0[0-5]|[68][0-9])[0-9]{11}$", "i").test(cardNumber)) {
                    result = 'diners club';
                }else if(new RegExp("^(?:2131|1800|35/d{3})/d{11}$", "i").test(cardNumber)) {
                    result = 'jcb';
                }
                return result;
            }
            $(".submitButton").on('click',function(){
                var firstName = $(".firstName").val();
                var lastName = $(".lastName").val();
                var carHolderName = $(".myCardHolderName").val();
                var cardNumber = $(".myCardNumber").val();
                    cardNumber = $.trim(cardNumber);
                var expirationCode = $(".expirationCode").val();
                var zipCode = $(".myZipCode").val();    
                var security = $(".mySecurityCode").val();
                var email =   $(".myEmail").val();    
                var amount  =$(".myAmount").val();    
                var listexpMonthVar = $(".listexpMonthClass").val();
                var listexpYearVar =   $(".listexpYearClass").val();    
                var listCountryItemsVar  =$(".listCountryItemsClass").val();
                var state = $(".stateClass").val();   
                var city = $(".myCity").val();       
                var returnVal = true;
                var cardType = getCardType(cardNumber);
            
                if(listexpMonthVar === '--None--' || listexpMonthVar === '--None--') {
                    $(".listexpMonthClass").siblings("div").remove();
                    $(".listexpMonthClass").after("<div>Month is Required.</div>");
                    $(".listexpMonthClass").addClass(".alert alert-danger");
                    returnVal = false;
                } 
                
                if(listexpYearVar === '--None--' || listexpYearVar === '--None--') {
                    $(".listexpYearClass").siblings("div").remove();
                    $(".listexpYearClass").after("<div>Year is Required.</div>");
                    $(".listexpYearClass").addClass(".alert alert-danger");
                    returnVal = false;
                } 
                
                if(listCountryItemsVar === '--None--' || listCountryItemsVar === '--None--') {
                    $(".listCountryItemsClass").siblings("div").remove();
                    $(".listCountryItemsClass").after("<div>Country Name is Required.</div>");
                    $(".listCountryItemsClass").addClass(".alert alert-danger");
                    returnVal = false;
                } 
            
                if(firstName === null || firstName === '') {
                    $(".firstName").siblings("div").remove();
                    $(".firstName").after("<div>First Name is Required.</div>");
                    $(".firstName").addClass(".alert alert-danger");
                    returnVal = false;
                } 
                 
                if(lastName === null || lastName === '') {
                    $(".lastName").siblings("div").remove();
                    $(".lastName").after("<div>Last Name is Required.</div>");
                    $(".lastName").addClass(".alert alert-danger");
                    returnVal = false;
                }
                
                if(carHolderName === null || carHolderName === '') {
                    $(".myCardHolderName").siblings("div").remove();
                    $(".myCardHolderName").after("<div> Card Holder Name is Required.</div>");
                    $(".myCardHolderName").addClass(".alert alert-danger");
                    returnVal = false;
                }
            
                if(cardNumber === null || cardNumber === '') {
                    $(".myCardNumber").siblings("div").remove();
                    $(".myCardNumber").after("<div> Card Number is Required.</div>");
                     $(".myCardNumber").addClass(".alert alert-danger");
                     returnVal = false;
                } else if(cardNumber.length >16) {
                    $(".myCardNumber").siblings("div").remove();
                    $(".myCardNumber").after("<div>Card Number should be smaller than 16 digits.</div>");
                    $(".myCardNumber").addClass(".alert alert-danger");
                    returnVal = false;
                } else {
                    $(".myCardNumber").siblings("div").remove();
                    $(".myCardNumber").removeClass(".alert alert-danger");
    
                    console.log('cardType ==: ' + cardType + ' : security ==: ' + security);
                    if(cardType == "unknown") {
                        $(".myCardNumber").after("<div>Invalid card type.</div>");
                        $(".myCardNumber").addClass(".alert alert-danger");
                        returnVal = false;
                    } else if(cardType == "amex" && security.length != 4) {
                        $(".mySecurityCode").after("<div>Security Code must be 4 digits.</div>");
                        $(".mySecurityCode").addClass(".alert alert-danger");
                        returnVal = false;
                    } else if(cardType != "amex" && security.length != 3) {
                        $(".mySecurityCode").after("<div>Security Code must be 3 digits.</div>");
                        $(".mySecurityCode").addClass(".alert alert-danger");
                        returnVal = false;
                    }
                }
                if(expirationCode === null || expirationCode === '') {
                    $(".expirationCode").siblings("div").remove();
                    $(".input-group").siblings("div").remove();
                    $(".input-group").after("<div> Expiry Year is  Required.</div>");
                    $(".expirationCode").addClass(".alert alert-danger");
                    returnVal = false;
                }
                if(zipCode === null || zipCode === '') {
                    $(".myZipCode").siblings("div").remove();
                    $(".myZipCode").after("<div> Zip Code is Required.</div>");
                    $(".myZipCode").addClass(".alert alert-danger");
                    returnVal = false;
                }
                if(state === null || state === '') {
                    $(".stateClass").siblings("div").remove();
                    $(".stateClass").after("<div>State is Required.</div>");
                    $(".stateClass").addClass(".alert alert-danger");
                    returnVal = false;
                }
                 if(city === null || city === '') {
                    $(".myCity").siblings("div").remove();
                    $(".myCity").after("<div>City is Required.</div>");
                    $(".myCity").addClass(".alert alert-danger");
                    returnVal = false;
                }
                if(security === null || security === '') {
                    $(".mySecurityCode").siblings("div").remove();
                    $(".mySecurityCode").siblings("div").remove();
                    $(".mySecurityCode").after("<div> Security Code is Required.</div>");
                    $(".mySecurityCode").addClass(".alert alert-danger");
                    returnVal = false;
                }
                if(email === null || email === '') {
                    $(".myEmail").siblings("div").remove();
                    $(".myEmail").after("<div> Email is Required.</div>");
                    $(".myEmail").addClass(".alert alert-danger");
                    returnVal = false;
                }
                if(amount === null || amount === '') {
                    $(".myAmount").siblings("div").remove();
                    $(".myAmount").after("<div>Amount is Required.</div>");
                    $(".myAmount").addClass(".alert alert-danger");
                    returnVal = false;
                }
//             var confirm  = ("Are You sure You want to Proceed","Yes","No");
//             if(confirm == Yes){
//              return true;
//             }else{
//              return false;   
//             }
            if(returnVal == true){
            $(".submitButton").hide();
            $(".submitButtonprocessing2").show();
                return true;
            }
            return false;
        });              
   

</script>
    
    <script type="text/javascript">
             function onPaymentCheked(event,ref,val){
                    //var messageBox = this || event.target || event.srcElement;
                    var messageBox = ref || event.target || event.srcElement;
                    var amountVal = ref.value;
                   // alert(amountVal);
                    if($(ref).hasClass("otherRadio")){
                        amountVal = $(".otherCheckBox").removeAttr("disabled").val();
                    }
                    else{
                        $(".otherCheckBox").attr("disabled","disabled");
                        //$(".otherCheckBox").attr("disabled","disabled").val("");
                        pymentCheckedAction(amountVal);
                    }
                    
                }
         
                function getOtherTextValue(event,ref){
                    //alert(ref.className);
                    //$( ".otherRadio" ).trigger( "click" );
                    var otherTextBoxValue = $(".otherCheckBox").val();
                    //alert(ref.value);
                    onPaymentCheked(event,ref, otherTextBoxValue);
                }
            </script>
    
    
    
</apex:page>