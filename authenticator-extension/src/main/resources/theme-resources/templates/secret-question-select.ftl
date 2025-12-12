<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
        Setup Secret Questions
    <#elseif section = "form">
        <form id="kc-secret-question-config-form" class="${properties.kcFormClass!}" action="${url.loginAction}" method="post">

            <div class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label class="${properties.kcLabelClass!}">Select which question to use for authentication</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <div style="margin: 10px 0;">
                        <label>
                            <input type="radio" name="selected_question" value="1" checked="checked" onchange="toggleAnswerFields()" />
                            What is your favorite food?
                        </label>
                    </div>
                    <div style="margin: 10px 0;">
                        <label>
                            <input type="radio" name="selected_question" value="2" onchange="toggleAnswerFields()" />
                            What is the name of your first pet?
                        </label>
                    </div>
                </div>
            </div>

            <div id="answer_field_1" class="${properties.kcFormGroupClass!}">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label class="${properties.kcLabelClass!}">What is your favorite food?</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input id="secret_answer_1" name="secret_answer_1" type="text" class="${properties.kcInputClass!}" placeholder="Enter your answer" />
                </div>
            </div>

            <div id="answer_field_2" class="${properties.kcFormGroupClass!}" style="display: none;">
                <div class="${properties.kcLabelWrapperClass!}">
                    <label class="${properties.kcLabelClass!}">What is the name of your first pet?</label>
                </div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input id="secret_answer_2" name="secret_answer_2" type="text" class="${properties.kcInputClass!}" placeholder="Enter your answer" />
                </div>
            </div>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <div class="${properties.kcFormButtonsWrapperClass!}">
                        <input type="hidden" id="selected_secret_question" name="selected_secret_question" />
                        <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doSubmit")}"/>
                    </div>
                </div>
            </div>
        </form>

        <script>
            function toggleAnswerFields() {
                var selected = document.querySelector('input[name="selected_question"]:checked').value;
                document.getElementById('answer_field_1').style.display = selected === '1' ? 'block' : 'none';
                document.getElementById('answer_field_2').style.display = selected === '2' ? 'block' : 'none';
                updateSelectedQuestion();
            }

            function updateSelectedQuestion() {
                var selected = document.querySelector('input[name="selected_question"]:checked').value;
                var questions = {
                    '1': 'What is your favorite food?',
                    '2': 'What is the name of your first pet?'
                };
                document.getElementById('selected_secret_question').value = questions[selected];
            }

            // Initialize on page load
            document.addEventListener('DOMContentLoaded', function() {
                updateSelectedQuestion();
            });
        </script>
    </#if>
</@layout.registrationLayout>
