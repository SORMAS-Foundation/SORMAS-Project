window.de_symeda_sormas_samples_AttributeExtension = function() {

    this.onStateChange = function() {
        var element = this.getElement(this.getParentId());
        if (element) {
            var attributes = this.getState().attributes;
            for (var attr in attributes) {
                if (attributes.hasOwnProperty(attr)) {
                        try {
                                element.setAttribute(attr, attributes[attr]);
                        } catch (e) {
                                // IE8 does not support type='number' - just ignore it
                        }
                }
            }
        }
    }
}
