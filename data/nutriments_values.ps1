$alphabet = 65..90 | foreach {[char] $_}
$header = $alphabet + ($alphabet | foreach {$1_ = $_; $alphabet | foreach {$1_ + $_}})
$header = $header[0..[array]::IndexOf($header, "FM")]
$headerOriginal = (Get-Content .\nutriments.csv)[0].split("`t")

for ($i = 0; $i -lt $header.length; $i++) {
    $header[$i] += "_" + $headerOriginal[$i]
}

$csv = Import-Csv .\nutriments.csv -Delimiter `t -Header $header -Encoding "utf8"

$stringBuilder = New-Object System.Text.StringBuilder
$query = "insert into nutriments_values (id_nutriments_aliment, id_nutriments_name, value, unit, matrix_unit, value_type) values "
$null = $stringBuilder.Append($query)

$csv = $csv[1..($csv.length - 1)]
$csv | foreach {
    $idAliment = $_.A_ID

    for ($i = 16; $i -lt $header.length - 16; $i += 4) {
        $idName = ($i - 16) / 4 + 1
        $value = $_.($header[$i]).replace(',', '.')
        $unit = $_.($header[$i + 1])
        $matrix_unit = $_.($header[$i + 2])
        $value_type = $_.($header[$i + 3])

        if ($value -and $unit -and $matrix_unit -and $value_type) {
            $null = $stringBuilder.Append("(`'$idAliment`', `'$idName`', `'$value`', `'$unit`', `'$matrix_unit`', `'$value_type`'),`r`n")
        }
    }    
}

$query = $stringBuilder.ToString()
$query = $query.Substring(0, $query.Length - 3)

$query > .\query.txt