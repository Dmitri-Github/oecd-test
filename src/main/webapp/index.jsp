<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title>OECD TestApp</title>
    <link rel="stylesheet" href="css/bootstrap-3.1.1.min.css">
    <style type="text/css">
        #send-mail-btn {
            margin-top: 30px;
            margin-left: 30px;
        }
        #myModalLabel {
            display: inline-block;
            margin-left: 20px;
        }
    </style>
</head>
<body>
    <button id="send-mail-btn" type="button" class="btn btn-primary" data-toggle="modal" data-target="#myModal">Send Mail</button>
    <!-- Modal -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <img src="img/logo-oecd.png" width="88" height="77">
                    <h4 class="modal-title" id="myModalLabel">Send Mail</h4>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label class="control-label" for="to-field">To:</label>
                            <input type="email" id="to-field" name="to" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label class="control-label" for="subject-field">Subject:</label>
                            <input type="text" id="subject-field" name="subject" class="form-control" required maxlength="128">
                        </div>
                        <div class="form-group">
                            <label class="control-label" for="body-field">Body:</label>
                            <input type="text" id="body-field" name="body" class="form-control" required>
                        </div>
                        <div class="col-sm-12" id="result"></div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="send()">Send</button>
                </div>
            </div>
        </div>
    </div>
    <%--Scripts--%>
    <script type="text/javascript" src="js/jquery-2.0.2.js"></script>
    <script src="//cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js"></script>
    <script type="text/javascript" src="js/vertxbus-2.1.js"></script>
    <script type="text/javascript" src="js/bootstrap-3.1.1.min.js"></script>
    <script type="text/javascript">
        var eb = new vertx.EventBus('http://localhost:8888/bridge');

        eb.onopen = function() {
            eb.registerHandler('epms.email.out', function(message) {
                $('#result').text(JSON.stringify(message));
            });
        }

        function send() {
            var to = $('#to-field').val();
            var subject = $('#subject-field').val();
            var body = $('#body-field').val();
            eb.send('epms.email.in', { to: to, subject: subject, body: body });
        }
    </script>
</body>
</html>