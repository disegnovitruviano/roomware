<?php

include './pickr_c.php'; 		// Controller

header('Content-Type: application/xml');
?>
<?php if ($output): ?>
<?php echo $output; ?>
<?php else: ?>
<<?= "" ?>?xml version="1.0" encoding="UTF-8"?<?= "" ?>>
<error>
	<message>No results</message>
</error>
<?php endif; ?>
