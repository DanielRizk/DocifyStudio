function toggleCollapse(id) {
    unhighlight();
    const content = document.getElementById(id);
    if (!content) return; // Check if the element exists

    const collapsible = content.previousElementSibling;
    if (!collapsible) return; // Check if the collapsible element exists

    if (content.style.display === 'block' || content.style.display === '') {
        content.style.display = 'none';
        collapsible.classList.remove('expanded');
    } else {
        content.style.display = 'block';
        collapsible.classList.add('expanded');
    }
    console.log('Toggled: ' + id);
}


function highlightSearch(text) {
    unhighlight(); // Unhighlight previous results
    window.findHighlightCalled = false; // Reset the scroll flag

    if (!text) return;

    var xpath = "//text()[contains(., '" + text + "')]";
    //var xpath = "//text()[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + text.toLowerCase() + "')]";
    var matches = document.evaluate(xpath, document, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);

    for (var i = 0; i < matches.snapshotLength; i++) {
        var textNode = matches.snapshotItem(i);
        expandParentCollapsible(textNode);
        highlightTextNode(textNode, text);
    }
}

function expandParentCollapsible(node) {
    while (node != null && node.tagName !== 'BODY') {
        if (node.classList && node.classList.contains('collapsible')) {
            var content = node.nextElementSibling;
            if (content && content.style && content.style.display !== 'block') {
                content.style.display = 'block';
                node.classList.add('expanded');
            }
        } else if (node.classList && node.classList.contains('emptyBox')) {
            var groupContent = node;
            if (groupContent && groupContent.style && groupContent.style.display !== 'block') {
                groupContent.style.display = 'block';
            }
        }
        node = node.parentNode;
    }
}


function highlightTextNode(node, text) {
    var boundary = "(?:\\b|(?<!\\w)[_\\[\\]]?)";
    var pattern = new RegExp(boundary + escapeRegExp(text) + boundary, "gi");
    var content = node.nodeValue;
    var match, matches = [];

    while ((match = pattern.exec(content)) !== null) {
        if (match.index === pattern.lastIndex) {
            pattern.lastIndex++;
        }

        // Check if the indices are within the range of the node's value
        var start = match.index;
        var end = start + match[0].length;
        if (end > content.length) break; // Prevent IndexSizeError

        var span = document.createElement('span');
        span.className = 'highlighted';
        var middleBit = node.splitText(start);
        var endBit = middleBit.splitText(end - start);
        var middleClone = middleBit.cloneNode(true);
        span.appendChild(middleClone);
        middleBit.parentNode.replaceChild(span, middleBit);

        matches.push(span); // Add the span to the matches array
        node = endBit; // Reset node for the next iteration

        // Adjust content for the remaining part
        content = node.nodeValue;
    }

    if (matches.length > 0 && !window.findHighlightCalled) {
        matches[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
        window.findHighlightCalled = true;
    }
}



function escapeRegExp(text) {
    return text.replace(/[-[\]/{}()*+?.\\^$|]/g, "\\$&");
}

function unhighlight() {
    const highlighted = document.querySelectorAll('.highlighted');
    highlighted.forEach(function(node) {
        const parent = node.parentNode;
        parent.replaceChild(document.createTextNode(node.textContent), node);
        parent.normalize();
    });
}