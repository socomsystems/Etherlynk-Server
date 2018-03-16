var widgetBlockInfo = (function(id) {
    var extended = {
        name: 'blockinfo',
        title: 'blockinfo',
        size: 'medium',
        widgetId: id, //needed for dashboard

        hideLink: true,

        customButtons: '<li><i class="show_detail fa fa-expand"></i></li>',


        template: _.template('<div class="info-table"> <table style="width: 100%; table-layout: fixed;" class="table table-striped"> ' +
            '<tbody><tr> <td  style="width: 120px;">number</td> <td class="value" contentEditable="false" style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"></td> </tr>' +
            '<tr> <td  style="width: 120px;">previous_hash</td> <td class="value" contentEditable="false" style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"></td> </tr>' +
            '<tr> <td  style="width: 120px;">data_hash</td> <td class="value" contentEditable="false" style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden;"></td> </tr>' +
            '<tr><td style="width: 100px;">Transactions</td><td style="text-overflow: ellipsis; overflow: hidden;"></td></tr>'+
            '</tbody> </table> <div>'),



        setData: function(data) {
            this.data = data;


            this.title = 'Block #' + this.data.a;
        },



        fetch: function() {


            var _this = this;

            $.when(




            ).fail(function (res) {



            }).done(function (res) {


                //Dashboard.render.widget(_this.name, _this.shell.tpl);
                //alert('I am blockinfo !!!!!');
                _this.title = 'Block #' + _this.data.a;

                $('#widget-' + _this.shell.id).css({
                    'height': '240px',
                    'margin-bottom': '10px',
                    'overflow-x': 'hidden',
                    'width': '100%'
                }).html( _this.template({
                    app: 'test1',
                    desc: 'testdata1',
                    numUser: 'dddd'
                }) );
                $('#widget-shell-' + _this.shell.id + ' .panel-title span').html(_this.title);

                $('#widget-shell-' + _this.shell.id + ' i.add-account').click(function(e) {

                    $.when(

                    ).done(function() {
                        openblockdetail('12');
                    });

                });

                _this.postRender();
                $(document).trigger("WidgetInternalEvent", ["widget|rendered|" + _this.name]);


            })


        },


    };


    var widget = _.extend({}, widgetRoot, extended);

    // register presence with screen manager
    Dashboard.addWidget(widget);
});
